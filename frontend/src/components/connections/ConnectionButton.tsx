import { useEffect, useState } from "react";
import keycloak from "../../features/auth/keycloak";
import { apiBaseUrl } from "../../config/runtimeConfig";
import "./connections.css";

type RelationshipStatus =
  | "NONE"
  | "PENDING_SENT"
  | "PENDING_RECEIVED"
  | "CONNECTED"
  | "BLOCKED_BY_ME"
  | "BLOCKED_BY_OTHER_USER";

type ConnectionStatusResponse = {
  targetUserId: string;
  status: RelationshipStatus;
  message: string;
};

type ApiResponse<T> = {
  message: string;
  data: T;
};

type Props = {
  targetUserId: string;
};

const BASE_URL = `${apiBaseUrl}/api/v1/connections`;

export default function ConnectionButton({ targetUserId }: Props) {
  const [status, setStatus] = useState<RelationshipStatus>("NONE");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  async function apiRequest<T>(url: string, options?: RequestInit): Promise<T> {
    if (keycloak.authenticated) {
      try {
        await keycloak.updateToken(30);
      } catch {
        // The request will fail normally if the token is expired.
      }
    }

    const response = await fetch(url, {
      ...options,
      headers: {
        "Content-Type": "application/json",
        Authorization: keycloak.token ? `Bearer ${keycloak.token}` : "",
        ...options?.headers,
      },
    });

    const result: ApiResponse<T> = await response.json();

    if (!response.ok) {
      throw new Error(result.message || "Something went wrong");
    }

    return result.data;
  }

  async function loadStatus() {
    try {
      setLoading(true);

      const data = await apiRequest<ConnectionStatusResponse>(
        `${BASE_URL}/status/${targetUserId}`
      );

      setStatus(data.status);
      setMessage(data.message);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "Failed to load status");
    } finally {
      setLoading(false);
    }
  }

  async function sendRequest() {
    try {
      setLoading(true);

      await apiRequest(`${BASE_URL}/requests`, {
        method: "POST",
        body: JSON.stringify({
          receiverId: targetUserId,
        }),
      });

      setStatus("PENDING_SENT");
      setMessage("Connection request sent");
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "Failed to send request");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadStatus();
  }, [targetUserId]);

  if (loading) {
    return <button className="connection-btn disabled">Loading...</button>;
  }

  return (
    <div>
      {status === "NONE" && (
        <button className="connection-btn primary" onClick={sendRequest}>
          Connect
        </button>
      )}

      {status === "PENDING_SENT" && (
        <button className="connection-btn secondary" disabled>
          Pending
        </button>
      )}

      {status === "PENDING_RECEIVED" && (
        <button className="connection-btn secondary" disabled>
          Respond in Requests
        </button>
      )}

      {status === "CONNECTED" && (
        <button className="connection-btn connected" disabled>
          Connected
        </button>
      )}

      {status === "BLOCKED_BY_ME" && (
        <button className="connection-btn danger" disabled>
          Blocked
        </button>
      )}

      {status === "BLOCKED_BY_OTHER_USER" && (
        <button className="connection-btn disabled" disabled>
          Unavailable
        </button>
      )}

      {message && <p className="connection-message">{message}</p>}
    </div>
  );
}
