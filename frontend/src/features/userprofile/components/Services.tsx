import React from "react";
import { Pencil } from "lucide-react";
import type { Profile } from "../api";

type ServicesProps = {
  profile?: Profile | null;
};

function Services({ profile }: ServicesProps) {
  const services = [profile?.industry, profile?.currentPosition].filter(
    (service): service is string => Boolean(service?.trim())
  );

  return (
    <div className="mt-5 rounded-xl border bg-white">
      <div className="p-8">
        <div className="flex items-start justify-between">
          <h2 className="text-3xl font-semibold">Services</h2>

          <Pencil size={28} className="cursor-pointer" />
        </div>

        <div className="mt-8 flex flex-wrap gap-4">
          {services.length > 0 ? (
            services.map((service) => (
              <span key={service} className="rounded-lg bg-gray-100 px-5 py-2 text-xl">
                {service}
              </span>
            ))
          ) : (
            <p className="text-gray-500">No services added yet.</p>
          )}
        </div>
      </div>
    </div>
  );
}

export default Services;
