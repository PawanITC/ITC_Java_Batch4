import React from "react";
import {
  Pencil,
  ArrowRight,
} from "lucide-react";

function Services() {
  const services = [
    "SaaS Development",
    "Software Testing",
    "Web Design",
    "Web Development",
  ];

  return (
    <div className="bg-white rounded-xl border mt-5">
      <div className="p-8">
        <div className="flex justify-between items-start">
          <h2 className="text-3xl font-semibold">
            Services
          </h2>

          <Pencil
            size={28}
            className="cursor-pointer"
          />
        </div>

        <div className="flex flex-wrap gap-4 mt-8">
          {services.map((service) => (
            <span
              key={service}
              className="px-5 py-2 bg-gray-100 rounded-lg text-xl"
            >
              {service}
            </span>
          ))}
        </div>
      </div>

      <div className="border-t py-6 flex justify-center">
        <button className="flex items-center gap-2 text-md font-semibold">
          Show all
          <ArrowRight />3
        </button>
      </div>
    </div>
  );
}

export default Services;