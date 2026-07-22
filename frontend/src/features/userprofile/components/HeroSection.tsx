import React, { useEffect, useState, useRef } from "react";
import { Pencil, X, Camera, Trash2 } from "lucide-react";
import type { Profile } from "../api";
import Avatar from "../../../components/common/Avatar";

type HeroSectionProps = {
  profile?: Profile | null;
  onProfileUpdate?: (updatedFields: Partial<Profile>) => Promise<void>;
  canEdit?: boolean;
};

type UploadTarget = "cover" | "avatar";

const HeroSection = ({ profile, onProfileUpdate, canEdit = true }: HeroSectionProps) => {
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [modalTarget, setModalTarget] = useState<UploadTarget>("cover");
  
  const [currentCover, setCurrentCover] = useState<string>(profile?.coverPhotoUrl || "");
  const [currentAvatar, setCurrentAvatar] = useState<string>(profile?.profilePictureUrl || "");

  const fileInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    setCurrentCover(profile?.coverPhotoUrl || "");
    setCurrentAvatar(profile?.profilePictureUrl || "");
  }, [profile?.coverPhotoUrl, profile?.profilePictureUrl]);

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (!canEdit || !onProfileUpdate) return;

    const file = event.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = async () => {
        const base64String = reader.result as string;
        
        if (modalTarget === "cover") {
          setCurrentCover(base64String);
          await onProfileUpdate({ coverPhotoUrl: base64String });
        } else {
          setCurrentAvatar(base64String);
          await onProfileUpdate({ profilePictureUrl: base64String });
        }
        setIsModalOpen(false);
      };
      reader.readAsDataURL(file);
    }
  };

  const openUploadModal = (target: UploadTarget) => {
    if (!canEdit) return;

    setModalTarget(target);
    setIsModalOpen(true);
  };

  const triggerGallery = () => {
    if (!canEdit) return;

    fileInputRef.current?.click();
  };

  const handleDeletePhoto = async () => {
    if (!canEdit || !onProfileUpdate) return;

    if (modalTarget === "cover") {
      setCurrentCover(""); 
      await onProfileUpdate({ coverPhotoUrl: "" });
    } else {
      setCurrentAvatar("");
      await onProfileUpdate({ profilePictureUrl: "" });
    }
    setIsModalOpen(false);
  };

  return (
    <div className="relative bg-white rounded-md shadow-sm border border-gray-200">
      {/* Hidden File Input element */}
      <input
        type="file"
        ref={fileInputRef}
        onChange={handleFileChange}
        accept="image/*"
        className="hidden"
      />

      {/* Main Hero Banner Wrapper */}
      <div className="w-full h-64 relative bg-[#1B2430] rounded-t-md">
        {currentCover ? (
          <img
            src={currentCover}
            alt="Cover"
            className="w-full h-full object-cover rounded-t-md"
          />
        ) : (
          <div className="w-full h-full bg-gradient-to-r from-slate-800 to-slate-900 rounded-t-md" />
        )}

        {/* Edit Cover Trigger Action Button (Pencil Icon) */}
        {canEdit && (
          <button
            onClick={() => openUploadModal("cover")}
            className="absolute right-6 top-4 h-10 w-10 rounded-full bg-white cursor-pointer flex items-center justify-center shadow hover:bg-gray-100 transition z-10"
            aria-label="Edit cover photo"
          >
            <Pencil size={18} className="text-gray-700" />
          </button>
        )}
      </div>

      {/* Profile Avatar Frame — Perfectly aligned layout anchor */}
      <div className="relative pl-10 h-24">
        <div 
          onClick={() => openUploadModal("avatar")}
          className={`absolute -top-24 left-10 rounded-full border-4 border-white shadow-md group relative overflow-hidden bg-white z-30 ${
            canEdit ? "cursor-pointer" : ""
          }`}
          style={{ width: "160px", height: "160px" }}
        >
          <Avatar
            name={`${profile?.firstName || ""} ${profile?.lastName || ""}`.trim() || "Your profile"}
            src={currentAvatar}
            sizeClassName="h-full w-full"
            textClassName="text-4xl font-bold tracking-wider text-[#1d4ed8]"
          />
          {canEdit && (
            <div className="absolute inset-0 bg-black/40 flex flex-col items-center justify-center opacity-0 group-hover:opacity-100 transition duration-200 rounded-full">
              <Camera size={26} className="text-white mb-0.5" />
              <span className="text-white text-xs font-semibold">Edit Photo</span>
            </div>
          )}
        </div>
      </div>

      {/* Shared Media Upload Modal Frame */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-60 p-4">
          <div className="w-full max-w-2xl bg-white rounded-xl shadow-2xl flex flex-col overflow-hidden">
            
            <div className="flex items-center justify-between px-6 py-4 border-b border-gray-200">
              <h2 className="text-xl font-semibold text-gray-900">
                {modalTarget === "cover" ? "Cover photo" : "Profile photo"}
              </h2>
              <button 
                onClick={() => setIsModalOpen(false)}
                className="p-1.5 hover:bg-gray-100 rounded-full text-gray-500 hover:text-gray-800 transition"
              >
                <X size={22} />
              </button>
            </div>

            <div className="bg-[#111] p-12 flex items-center justify-center min-h-[260px] relative">
              {modalTarget === "cover" ? (
                currentCover ? (
                  <img src={currentCover} alt="Cover Preview" className="max-h-56 w-full object-contain" />
                ) : (
                  <span className="text-gray-500 font-medium text-sm">No cover photo set</span>
                )
              ) : (
                currentAvatar ? (
                  <img src={currentAvatar} alt="Avatar Preview" className="h-44 w-44 rounded-full object-cover border-2 border-white" />
                ) : (
                  <div className="h-44 w-44 rounded-full bg-[#E8F5E9] flex items-center justify-center border-2 border-white">
                    <span className="text-[#2E7D32] text-5xl font-bold">
                      {((profile?.firstName?.[0] || "") + (profile?.lastName?.[0] || "")).toUpperCase() || "HA"}
                    </span>
                  </div>
                )
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
};

export default HeroSection;
