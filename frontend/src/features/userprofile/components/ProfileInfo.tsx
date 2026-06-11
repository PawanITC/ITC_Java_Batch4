import React from "react";
import {
  ShieldCheck,
  MapPin,
} from "lucide-react";

import companyphoto from "../../../assets/profile.jpeg"

function ProfileInfo() {
  return (
    <div className="bg-white p-8 flex justify-between">
      {/* Left Section */}
      <div className="flex-1">
        {/* Name + Badge */}
        <div className="flex items-center gap-3 flex-wrap">
          <h1 className="text-2xl font-semibold">Hasnain Ahmad</h1>

          <span className="text-xl text-gray-600">
            He/Him
          </span>

          <button className="flex items-center gap-2 border-2 border-dashed border-blue-600 rounded-full px-5 py-1 text-blue-700 font-semibold">
            <ShieldCheck size={15} />
            Add verification badge
          </button>
        </div>

        {/* Title */}
        <p className="text-xl mt-2 text-gray-900">
          Software Engineer | React js | Node js
        </p>

        {/* Location */}
        <div className="flex items-center gap-2 mt-2 text-gray-500 text-xl">
          <MapPin size={18} />

          <span>
            Crawley, England, United Kingdom
          </span>

          <span>·</span>

          <button className="text-blue-700 font-semibold">
            Contact info
          </button>
        </div>

        {/* Connections */}
        <p className="text-blue-700 font-semibold text-xl mt-6">
          500+ connections
        </p>

        {/* Buttons */}
        <div className="flex gap-4 mt-8 flex-wrap">
          <button className="bg-blue-700 text-white px-7 py-2 rounded-full text-xl font-semibold">
            Open to
          </button>

          <button className="border-2 border-blue-700 text-blue-700 px-7 py-1 rounded-full text-xl font-semibold">
            Add section
          </button>

          <button className="border-2 border-blue-700 text-blue-700 px-7 py-1 rounded-full text-xl font-semibold">
            Enhance profile
          </button>

          <button className="border-2 border-gray-600 text-gray-700 px-7 py-1 rounded-full text-xl font-semibold">
            Resources
          </button>
        </div>
      </div>

      {/* Right Section */}
      <div className="w-80 ml-10 space-y-8">
        {/* Company */}
        <div className="flex items-center gap-4">
          <img
            src={companyphoto}
            alt="Fiverr"
            className="w-16 h-16 object-contain rounded-full"
          />

          <h3 className="text-xl font-semibold">
            Fiverr
          </h3>
        </div>

        {/* University */}
        <div className="flex items-center gap-4">
          <img
            src={companyphoto}
            alt="Comsats"
            className="w-16 h-16 object-contain rounded-full"
          />

          <h3 className="text-xl font-semibold leading-tight">
            COMSATS University
            <br />
            Islamabad
          </h3>
        </div>
      </div>
    </div>
  );
}

export default ProfileInfo;