import { type ChangeEvent, type FormEvent, useCallback, useEffect, useState } from "react";
import { useParams } from "react-router-dom";
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
  const { profileId } = useParams();
  const [profile, setProfile] = useState<Profile | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [needsOnboarding, setNeedsOnboarding] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const { isLoggedIn, loading: authLoading, user: authUser } = useAppSelector(
    (state) => state.auth
  );
  const isViewingOwnProfile = !profileId;

  const loadProfile = useCallback(async () => {
    if (authLoading) return;

    setLoading(true);
    setError(null);
    setNeedsOnboarding(false);

    if (!isLoggedIn) {
      setProfile(null);
      setLoading(false);
      return;
    }

    if (profileId) {
      try {
        const selectedProfile = await getProfile(profileId);
        setProfile(selectedProfile);
      } catch (loadError) {
        console.error(loadError);
        setProfile(null);
        setError("Unable to load this profile right now.");
      } finally {
        setLoading(false);
      }
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

        setProfile(null);
        setNeedsOnboarding(true);
        return;
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
  }, [authLoading, authUser, isLoggedIn, profileId]);

  useEffect(() => {
    loadProfile();
  }, [loadProfile]);

  const handleCreateProfile = async (payload: CreateProfilePayload) => {
    setError(null);
    const createdProfile = await createProfile(payload);
    const hydratedProfile = await getProfile(createdProfile.id);
    setProfile(hydratedProfile);
    setNeedsOnboarding(false);
  };

  if (loading) {
    return <div className="min-h-screen bg-[#F4F2EE] px-6 py-10">Loading profile...</div>;
  }

  if (error) {
    return <div className="min-h-screen bg-[#F4F2EE] px-6 py-10">{error}</div>;
  }

  if (needsOnboarding) {
    return (
      <ProfileOnboarding
        authEmail={getKeycloakUser()?.email || authUser?.email || ""}
        authFirstName={getKeycloakUser()?.firstName || ""}
        authLastName={getKeycloakUser()?.lastName || ""}
        onCreate={handleCreateProfile}
      />
    );
  }

  if (!profile) {
    return (
      <div className="min-h-screen bg-[#F4F2EE] px-6 py-10">
        Sign in with Keycloak to create your profile.
      </div>
    );
  }

  return (
    <div className="w-full min-h-screen bg-[#F4F2EE] py-10 pb-24">
      <div className="w-[65%] mx-auto flex flex-col gap-6">
        <HeroSection profile={profile} />
        <ProfileInfo
          profile={profile}
          onEdit={() => setIsEditModalOpen(true)}
          canEdit={isViewingOwnProfile}
        />
        <About
          profile={profile}
          onEdit={() => setIsEditModalOpen(true)}
          canEdit={isViewingOwnProfile}
        />
        <Services profile={profile} />
        <Experience
          experiences={profile.experiences || []}
          profileId={profile.id}
          onRefresh={loadProfile}
          canEdit={isViewingOwnProfile}
        />
        <Education profile={profile} onRefresh={loadProfile} canEdit={isViewingOwnProfile} />
        <Skills
          skills={profile.skills || []}
          profileId={profile.id}
          onRefresh={loadProfile}
          canEdit={isViewingOwnProfile}
        />
        {isViewingOwnProfile && (
          <ProfileEditModal
            isOpen={isEditModalOpen}
            onClose={() => setIsEditModalOpen(false)}
            profile={profile}
            onSaved={loadProfile}
          />
        )}
      </div>
    </div>
  );
}

type ProfileOnboardingProps = {
  authEmail: string;
  authFirstName: string;
  authLastName: string;
  onCreate: (payload: CreateProfilePayload) => Promise<void>;
};

function ProfileOnboarding({
  authEmail,
  authFirstName,
  authLastName,
  onCreate,
}: ProfileOnboardingProps) {
  const [values, setValues] = useState({
    firstName: authFirstName,
    lastName: authLastName,
    email: authEmail,
    gender: "PREFER_NOT_TO_SAY",
    headline: "",
    about: "",
    city: "",
    country: "",
    openToWork: false,
    profilePublic: true,
  });
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const handleChange = (
    event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    const { name, value } = event.target;
    setValues((current) => ({ ...current, [name]: value }));
  };

  const handleCheckboxChange = (event: ChangeEvent<HTMLInputElement>) => {
    const { name, checked } = event.target;
    setValues((current) => ({ ...current, [name]: checked }));
  };

  const optional = (value: string) => {
    const trimmed = value.trim();
    return trimmed ? trimmed : undefined;
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();

    try {
      setSaving(true);
      setError("");
      await onCreate({
        firstName: values.firstName.trim(),
        lastName: values.lastName.trim(),
        email: values.email.trim(),
        gender: values.gender,
        headline: optional(values.headline),
        about: optional(values.about),
        city: optional(values.city),
        country: optional(values.country),
        openToWork: values.openToWork,
        profilePublic: values.profilePublic,
      });
    } catch (createError) {
      console.error(createError);
      setError("Unable to create your profile. Please check the fields and try again.");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#F4F2EE] px-4 py-10">
      <form
        onSubmit={handleSubmit}
        className="mx-auto max-w-2xl rounded-lg border border-[#d6d6d6] bg-white p-6 shadow-sm"
      >
        <h1 className="text-2xl font-semibold text-gray-900">Create your profile</h1>
        <p className="mt-1 text-sm text-gray-600">
          Add the details you want people to see. Your account ownership is taken from Keycloak.
        </p>

        {error && <p className="mt-4 rounded bg-red-50 px-3 py-2 text-sm text-red-700">{error}</p>}

        <div className="mt-6 grid grid-cols-1 gap-4 md:grid-cols-2">
          <label className="text-sm font-medium text-gray-700">
            First name*
            <input
              name="firstName"
              value={values.firstName}
              onChange={handleChange}
              required
              minLength={2}
              maxLength={50}
              className="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2"
            />
          </label>

          <label className="text-sm font-medium text-gray-700">
            Last name*
            <input
              name="lastName"
              value={values.lastName}
              onChange={handleChange}
              required
              minLength={2}
              maxLength={50}
              className="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2"
            />
          </label>
        </div>

        <div className="mt-4 grid grid-cols-1 gap-4 md:grid-cols-2">
          <label className="text-sm font-medium text-gray-700">
            Email*
            <input
              name="email"
              type="email"
              value={values.email}
              onChange={handleChange}
              required
              className="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2"
            />
          </label>

          <label className="text-sm font-medium text-gray-700">
            Gender*
            <select
              name="gender"
              value={values.gender}
              onChange={handleChange}
              required
              className="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2"
            >
              <option value="PREFER_NOT_TO_SAY">Prefer not to say</option>
              <option value="MALE">Male</option>
              <option value="FEMALE">Female</option>
              <option value="OTHER">Other</option>
            </select>
          </label>
        </div>

        <label className="mt-4 block text-sm font-medium text-gray-700">
          Headline
          <input
            name="headline"
            value={values.headline}
            onChange={handleChange}
            maxLength={220}
            className="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2"
          />
        </label>

        <label className="mt-4 block text-sm font-medium text-gray-700">
          About
          <textarea
            name="about"
            value={values.about}
            onChange={handleChange}
            rows={4}
            maxLength={5000}
            className="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2"
          />
        </label>

        <div className="mt-4 grid grid-cols-1 gap-4 md:grid-cols-2">
          <label className="text-sm font-medium text-gray-700">
            City
            <input
              name="city"
              value={values.city}
              onChange={handleChange}
              className="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2"
            />
          </label>

          <label className="text-sm font-medium text-gray-700">
            Country
            <input
              name="country"
              value={values.country}
              onChange={handleChange}
              className="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2"
            />
          </label>
        </div>

        <div className="mt-5 space-y-3">
          <label className="flex items-center gap-3 text-sm text-gray-700">
            <input
              type="checkbox"
              name="openToWork"
              checked={values.openToWork}
              onChange={handleCheckboxChange}
            />
            Open to work
          </label>

          <label className="flex items-center gap-3 text-sm text-gray-700">
            <input
              type="checkbox"
              name="profilePublic"
              checked={values.profilePublic}
              onChange={handleCheckboxChange}
            />
            Public profile
          </label>
        </div>

        <div className="mt-6 flex justify-end">
          <button
            type="submit"
            disabled={saving}
            className="rounded-full bg-[#0a66c2] px-6 py-2 text-sm font-semibold text-white hover:bg-[#004182] disabled:opacity-60"
          >
            {saving ? "Creating..." : "Create profile"}
          </button>
        </div>
      </form>
    </div>
  );
}

export default UserProfile;
