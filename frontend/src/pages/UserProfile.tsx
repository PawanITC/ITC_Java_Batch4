import React from "react";
import { Pencil } from 'lucide-react';
import HeroSection from "../features/userprofile/components/HeroSection";
import ProfileInfo from "../features/userprofile/components/ProfileInfo";
import About from "../features/userprofile/components/About";
import Services from "../features/userprofile/components/Services";
import Education from "../features/userprofile/components/Education";
import Experience from "../features/userprofile/components/Experience";

function UserProfile() {
  return (
    <div className="w-[65%] mx-auto h-screen mt-10">
      <HeroSection/>
      <ProfileInfo/>
      <About/>
      <Services/>
      <Experience/>
      <Education/>
    </div>
  );
}

export default UserProfile;
