import { useCallback, useEffect, useState } from "react";
import { useAppSelector } from "../hooks/reduxHooks";
import { getKeycloakUser } from "../features/auth/keycloakUser";
import About from "../features/userprofile/components/About";
import Education from "../features/userprofile/components/Education";
import Experience from "../features/userprofile/components/Experience";
import HeroSection from "../features/userprofile/components/HeroSection";
import ProfileInfo from "../features/userprofile/components/ProfileInfo";
import ProfileEditModal from "../features/userprofile/components/ProfileEditModal";
import Services from "../features/userprofile/components/Services";
import Skills from "../features/userprofile/components/Skills";
import {
  createProfile,
  getCurrentProfile,
  getProfile,
  type CreateProfilePayload,
  type Profile,
} from "../features/userprofile/api";

function UserProfile() {
  const [profile, setProfile] = useState<Profile | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const { isLoggedIn, loading: authLoading, user: authUser } = useAppSelector(
    (state) => state.auth
  );

  const buildProfilePayload = useCallback((): CreateProfilePayload | null => {
    const keycloakUser = getKeycloakUser();
    const email = keycloakUser?.email || authUser?.email;

    if (!email) {
      return null;
    }

    return {
      keycloakUserId: keycloakUser?.id,
      firstName: keycloakUser?.firstName || authUser?.name || "LinkedIn",
      lastName: keycloakUser?.lastName || "Member",
      email,
      headline: "Profile in progress",
      about: "This profile was created from the signed-in account.",
      gender: "PREFER_NOT_TO_SAY",
      city: "London",
      country: "UK",
      openToWork: true,
      profilePublic: true,
    };
  }, [authUser]);

  const loadProfile = useCallback(async () => {
    if (authLoading) return;

    setLoading(true);
    setError(null);

    if (!isLoggedIn) {
      setProfile(null);
      setLoading(false);
      return;
    }

    const email = (getKeycloakUser()?.email || authUser?.email)?.toLowerCase();

    if (!email) {
      setProfile(null);
      setError("Your signed-in Keycloak account does not include an email address.");
      setLoading(false);
      return;
    }

    try {
      let selectedProfile: Profile | null = null;

      try {
        selectedProfile = await getCurrentProfile();
      } catch (profileError: any) {
        if (profileError?.response?.status !== 404) {
          throw profileError;
        }

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
  }, [authLoading, authUser, buildProfilePayload, isLoggedIn]);

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
        Sign in with Keycloak to provision your profile automatically.
      </div>
    );
  }

  return (
    <div className="w-full min-h-screen bg-[#F4F2EE] py-10 pb-24">
      <div className="w-[65%] mx-auto flex flex-col gap-6">
        <HeroSection profile={profile} />
        <ProfileInfo profile={profile} onEdit={() => setIsEditModalOpen(true)} />
        <About profile={profile} onEdit={() => setIsEditModalOpen(true)} />
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
        <ProfileEditModal
          isOpen={isEditModalOpen}
          onClose={() => setIsEditModalOpen(false)}
          profile={profile}
          onSaved={loadProfile}
        />
      </div>
    </div>
  );
}

export default UserProfile;
