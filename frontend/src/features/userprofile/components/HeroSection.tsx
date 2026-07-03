
import React, { useState, useRef } from "react";
import { Pencil, X, Camera, Trash2 } from "lucide-react";
import profileimage from "../../../assets/profile.jpeg";
import coverimage from "../../../assets/cover.jpeg";
import type { Profile } from "../api";

type HeroSectionProps = {
  profile?: Profile | null;
};

const HeroSection = ({ profile }: HeroSectionProps) => {
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  
  const [currentCover, setCurrentCover] = useState<string>(
    profile?.coverPhotoUrl || coverimage
  );

  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setCurrentCover(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  const triggerGallery = () => {
    fileInputRef.current?.click();
  };

  const handleDeletePhoto = () => {
    setCurrentCover(""); 
    setIsModalOpen(false);
  };

  return (
    <div className="relative bg-white rounded-md">
      {/* Hidden File Input element tracking image uploads */}
      <input
        type="file"
        ref={fileInputRef}
        onChange={handleFileChange}
        accept="image/*"
        className="hidden"
      />

      {/* Main Hero Banner Wrapper */}
      <div className="w-full h-64 relative bg-gray-900">
        {currentCover ? (
          <img
            src={currentCover}
            alt="Cover"
            className="w-full h-full object-cover"
          />
        ) : (
          <div className="w-full h-full bg-gradient-to-r from-slate-800 to-slate-900" />
        )}

        {/* Profile Avatar Frame */}
        <img
          src={profile?.profilePictureUrl || profileimage}
          alt="Profile"
          className="w-40 h-40 rounded-full object-cover absolute top-48 ml-10 border-4 border-white shadow-sm"
        />

        {/* Edit Cover Trigger Action Button (Pencil Icon) */}
        <button
          onClick={() => setIsModalOpen(true)}
          className="absolute right-6 top-4 h-10 w-10 rounded-full bg-white cursor-pointer flex items-center justify-center shadow hover:bg-gray-100 transition"
          aria-label="Edit cover photo"
        >
          <Pencil size={18} className="text-gray-700" />
        </button>
      </div>

      <div className="h-28" />

     
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-60 p-4">
          <div className="w-full max-w-2xl bg-white rounded-xl shadow-2xl flex flex-col overflow-hidden animate-fadeIn">
            
            <div className="flex items-center justify-between px-6 py-4 border-b border-gray-200">
              <h2 className="text-xl font-semibold text-gray-900">Cover photo</h2>
              <button 
                onClick={() => setIsModalOpen(false)}
                className="p-1.5 hover:bg-gray-100 rounded-full text-gray-500 hover:text-gray-800 transition"
              >
                <X size={22} />
              </button>
            </div>

            <div className="bg-[#111] p-12 flex items-center justify-center min-h-[260px] relative">
              {currentCover ? (
                <img 
                  src={currentCover} 
                  alt="Cover Preview" 
                  className="max-h-56 w-full object-contain"
                />
              ) : (
                <span className="text-gray-500 font-medium text-sm">No cover photo set</span>
              )}
            </div>

            <div className="flex items-center justify-around bg-white border-t border-gray-100 py-4">
              
              <button
                onClick={triggerGallery}
                className="flex flex-col items-center gap-1 group text-gray-600 hover:text-blue-600 font-medium text-sm transition"
              >
                <div className="p-2.5 rounded-full group-hover:bg-blue-50 transition">
                  <Camera size={22} className="text-blue-600" />
                </div>
                <span>Change photo</span>
              </button>

              <button
                onClick={handleDeletePhoto}
                className="flex flex-col items-center gap-1 group text-gray-600 hover:text-red-600 font-medium text-sm transition"
              >
                <div className="p-2.5 rounded-full group-hover:bg-red-50 transition">
                  <Trash2 size={22} className="text-red-500" />
                </div>
                <span>Delete</span>
              </button>

            </div>

          </div>
        </div>
      )}
    </div>
  );
}

export default HeroSection;
