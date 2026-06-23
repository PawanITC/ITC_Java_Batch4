import HeroSection from "../features/userprofile/components/HeroSection";
import ProfileInfo from "../features/userprofile/components/ProfileInfo";
import About from "../features/userprofile/components/About";
import Services from "../features/userprofile/components/Services";
import Experience from "../features/userprofile/components/Experience";
import Education from "../features/userprofile/components/Education";
import Skills from "../features/userprofile/components/Skills"; // Import the new skills component
import { useEffect, useState } from "react";
import axios from "axios";

function UserProfile() {
  const [profile, setProfile] = useState<any>(null);
  const [experiences, setExperiences] = useState<any[]>([]);
  const [skills, setSkills] = useState<any[]>([]);

  useEffect(() => {
    axios.get("http://localhost:8083/api/profiles/5a030dc0-d38f-4952-a378-980f404d877e")
      .then(response => {
        const data = response.data;
        setProfile(data);
        setExperiences(data.experiences || []);
        setSkills(data.skills || []); 
      })
      .catch(err => console.error("Error fetching data:", err));
  }, []);

  return (
    <div className="w-full min-h-screen bg-[#F4F2EE] py-10 pb-24">
      <div className="w-[65%] mx-auto flex flex-col gap-6">
        <HeroSection />
        <ProfileInfo profile={profile} />
        <About />
        <Services />
        <Experience experiences={experiences} />
        <Education profile={profile} />
        <Skills skills={skills} /> 

      </div>
    </div>
  );
}

export default UserProfile;