import HeroSection from "../features/userprofile/components/HeroSection";
import ProfileInfo from "../features/userprofile/components/ProfileInfo";
import About from "../features/userprofile/components/About";
import Services from "../features/userprofile/components/Services";
import Education from "../features/userprofile/components/Education";
import Experience from "../features/userprofile/components/Experience";
import { useEffect, useState } from "react";
import axios from "axios"; // 1. Imported Axios here

function UserProfile() {
  const [profile, setProfile] = useState<any>(null);
  const [experiences, setExperiences] = useState<any[]>([]);

  useEffect(() => {
    axios.get("http://localhost:8083/api/profiles/5a030dc0-d38f-4952-a378-980f404d877e")
      .then(response => {
        const data = response.data;
        setProfile(data);
        setExperiences(data.experiences || []);
      })
      .catch(err => {
        console.error("Error pulling profile via Axios:", err);
      });
  }, []);

  return (
    <div className="w-full min-h-screen bg-[#F4F2EE] py-10">
      
      {/* Content layout wrapper constraint */}
      <div className="w-[65%] mx-auto space-y-6">
        <HeroSection />

        <ProfileInfo profile={profile} />

        <About />

        <Services />

        <Experience experiences={experiences} />

        <Education profile={profile} />
      </div>

    </div>
  );
}

export default UserProfile;