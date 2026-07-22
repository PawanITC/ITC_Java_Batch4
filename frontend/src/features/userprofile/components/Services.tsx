import React from "react";
import { Pencil } from "lucide-react";
import type { Profile } from "../api";

type ServicesProps = {
  profile?: Profile | null;
  canEdit?: boolean;
};

function Services({ profile, canEdit = true }: ServicesProps) {
  const services = [profile?.industry, profile?.currentPosition].filter(
    (service): service is string => Boolean(service?.trim())
  );

  if (!canEdit && services.length === 0) {
    return null;
  }

  return (
    <div className="mt-5 rounded-lg border border-gray-200 bg-white shadow-sm">
      <div className="p-8">
        <div className="flex items-start justify-between">
          <h2 className="text-2xl font-semibold text-gray-950">Services</h2>

          {canEdit && (
            <button
              type="button"
              className="rounded-full p-2 text-gray-600 transition-colors hover:bg-gray-100"
              title="Edit services"
            >
              <Pencil size={20} />
            </button>
          )}
        </div>

        <div className="mt-6 flex flex-wrap gap-3">
          {services.length > 0 ? (
            services.map((service) => (
              <span
                key={service}
                className="rounded-full border border-gray-200 bg-slate-50 px-4 py-2 text-sm font-semibold text-gray-800"
              >
                {service}
              </span>
            ))
          ) : (
            canEdit && <p className="text-gray-500">No services added yet.</p>
          )}
        </div>
      </div>
    </div>
  );
}

export default Services;
