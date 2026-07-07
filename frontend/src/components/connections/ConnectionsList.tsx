import { useEffect, useState } from "react";
import "./connections.css";

type ConnectionResponse = {
  id: string;
  requesterId: string;
  receiverId: string;
  status: string;
  requestedAt: string;
  respondedAt: string | null;
};

type PageResponse<T> = {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
};

type ApiResponse<T> = {
  message: string;
  data: T;
};

type TabType = "connections" | "received" | "sent";

const BASE_URL = "http://localhost:8085/api/v1/connections";

function getToken() {
  return localStorage.getItem("access_token");
}

export default function ConnectionsList() {
  const [tab, setTab] = useState<TabType>("connections");
  const [items, setItems] = useState<ConnectionResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  async function apiRequest<T>(url: string, options?: RequestInit): Promise<T> {
    const token = getToken();

    const response = await fetch(url, {
      ...options,
      headers: {
        "Content-Type": "application/json",
        Authorization: token ? `Bearer ${token}` : "",
        ...options?.headers,
      },
    });

    const result: ApiResponse<T> = await response.json();

    if (!response.ok) {
      throw new Error(result.message || "Something went wrong");
    }

    return result.data;
  }

  async function loadConnections(selectedTab: TabType) {
    try {
      setLoading(true);
      setMessage("");

      let url = "";

      if (selectedTab === "connections") {
        url = `${BASE_URL}/me`;
      }

      if (selectedTab === "received") {
        url = `${BASE_URL}/requests/received`;
      }

      if (selectedTab === "sent") {
        url = `${BASE_URL}/requests/sent`;
      }

      const data = await apiRequest<PageResponse<ConnectionResponse>>(url);
      setItems(data.content);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "Failed to load data");
    } finally {
      setLoading(false);
    }
  }

  async function acceptRequest(requestId: string) {
    try {
      await apiRequest(`${BASE_URL}/requests/${requestId}/accept`, {
        method: "PATCH",
      });

      setMessage("Request accepted");
      loadConnections(tab);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "Failed to accept request");
    }
  }

  async function rejectRequest(requestId: string) {
    try {
      await apiRequest(`${BASE_URL}/requests/${requestId}/reject`, {
        method: "PATCH",
      });

      setMessage("Request rejected");
      loadConnections(tab);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "Failed to reject request");
    }
  }

  async function cancelRequest(requestId: string) {
    try {
      await apiRequest(`${BASE_URL}/requests/${requestId}/cancel`, {
        method: "PATCH",
      });

      setMessage("Request cancelled");
      loadConnections(tab);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "Failed to cancel request");
    }
  }

  useEffect(() => {
    loadConnections(tab);
  }, [tab]);

  return (
    <div className="connections-container">
      <h2>Connections</h2>

      <div className="connections-tabs">
        <button
          className={tab === "connections" ? "tab active" : "tab"}
          onClick={() => setTab("connections")}
        >
          My Connections
        </button>

        <button
          className={tab === "received" ? "tab active" : "tab"}
          onClick={() => setTab("received")}
        >
          Received Requests
        </button>

        <button
          className={tab === "sent" ? "tab active" : "tab"}
          onClick={() => setTab("sent")}
        >
          Sent Requests
        </button>
      </div>

      {message && <p className="connection-alert">{message}</p>}

      {loading && <p>Loading...</p>}

      {!loading && items.length === 0 && <p>No data found.</p>}

      <div className="connections-list">
        {items.map((item) => (
          <div className="connection-card" key={item.id}>
            <div className="avatar">U</div>

            <div className="connection-info">
              {tab === "received" && <h3>Requester User</h3>}
              {tab === "sent" && <h3>Receiver User</h3>}
              {tab === "connections" && <h3>Connected User</h3>}

              <p>
                <strong>Requester:</strong> {item.requesterId}
              </p>

              <p>
                <strong>Receiver:</strong> {item.receiverId}
              </p>

              <p>
                <strong>Status:</strong> {item.status}
              </p>
            </div>

            <div className="connection-actions">
              {tab === "received" && (
                <>
                  <button
                    className="connection-btn primary"
                    onClick={() => acceptRequest(item.id)}
                  >
                    Accept
                  </button>

                  <button
                    className="connection-btn secondary"
                    onClick={() => rejectRequest(item.id)}
                  >
                    Ignore
                  </button>
                </>
              )}

              {tab === "sent" && (
                <button
                  className="connection-btn secondary"
                  onClick={() => cancelRequest(item.id)}
                >
                  Cancel
                </button>
              )}

              {tab === "connections" && (
                <button className="connection-btn connected" disabled>
                  Connected
                </button>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}