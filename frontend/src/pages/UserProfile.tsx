import { useCallback, useEffect, useState } from "react";
import { useAppSelector } from "../hooks/reduxHooks";
import keycloak from "../features/auth/keycloak";
import About from "../features/userprofile/components/About";
import Education from "../features/userprofile/components/Education";
import Experience from "../features/userprofile/components/Experience";
import HeroSection from "../features/userprofile/components/HeroSection";
import ProfileInfo from "../features/userprofile/components/ProfileInfo";
import Services from "../features/userprofile/components/Services";
import Skills from "../features/userprofile/components/Skills";
import {
  createProfile,
  getProfile,
  listProfiles,
  type CreateProfilePayload,
  type Profile,
} from "../features/userprofile/api";

function UserProfile() {
  const [profile, setProfile] = useState<Profile | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { isLoggedIn } = useAppSelector((state) => state.auth);

  const buildProfilePayload = useCallback((): CreateProfilePayload | null => {
    const token = keycloak.tokenParsed as
      | {
          email?: string;
          given_name?: string;
          family_name?: string;
          preferred_username?: string;
        }
      | undefined;

    if (!token?.email) {
      return null;
    }

    return {
      firstName: token.given_name || token.preferred_username || "Demo",
      lastName: token.family_name || "User",
      email: token.email,
      headline: "Profile in progress",
      about: "This profile was created from the signed-in account.",
      gender: "PREFER_NOT_TO_SAY",
      city: "London",
      country: "UK",
      openToWork: true,
      profilePublic: true,
    };
  }, []);

  const loadProfile = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const profiles = await listProfiles();
      const token = keycloak.tokenParsed as
        | { email?: string; preferred_username?: string }
        | undefined;
      const email = token?.email?.toLowerCase();

      let selectedProfile =
        profiles.find((item) => item.email?.toLowerCase() === email) || profiles[0] || null;

      if (!selectedProfile && isLoggedIn) {
        const payload = buildProfilePayload();
        if (payload) {
          selectedProfile = await createProfile(payload);
        }
      }

      if (!selectedProfile) {
        setProfile(null);
        return;
      }

      const hydratedProfile = await getProfile(selectedProfile.id);
      setProfile(hydratedProfile);
    } catch (loadError) {
      console.error(loadError);
      setError("Unable to load the profile right now.");
    } finally {
      setLoading(false);
    }
  }, [buildProfilePayload, isLoggedIn]);

  useEffect(() => {
    loadProfile();
  }, [loadProfile]);

  if (loading) {
    return <div className="min-h-screen bg-[#F4F2EE] px-6 py-10">Loading profile...</div>;
  }

  if (error) {
    return <div className="min-h-screen bg-[#F4F2EE] px-6 py-10">{error}</div>;
  }

  if (!profile) {
    return (
      <div className="min-h-screen bg-[#F4F2EE] px-6 py-10">
        No profile exists yet. Sign in to provision one automatically.
      </div>
    );
  }

  return (
    <div className="w-full min-h-screen bg-[#F4F2EE] py-10 pb-24">
      <div className="w-[65%] mx-auto flex flex-col gap-6">
        <HeroSection />
        <ProfileInfo profile={profile} />
        <About />
        <Services />
        <Experience
          experiences={profile.experiences || []}
          profileId={profile.id}
          onRefresh={loadProfile}
        />
        <Education profile={profile} onRefresh={loadProfile} />
        <Skills
          skills={profile.skills || []}
          profileId={profile.id}
          onRefresh={loadProfile}
        />
      </div>
    </div>
  );
}

export default UserProfile;
