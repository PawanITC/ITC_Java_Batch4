import React, { useEffect, useState } from "react";
import { X } from "lucide-react";
import type { Profile, UpdateProfilePayload } from "../api";
import { updateProfile } from "../api";

type ProfileEditModalProps = {
  isOpen: boolean;
  onClose: () => void;
  profile: Profile;
  onSaved: () => Promise<void>;
};

const emptyToUndefined = (value: string) => {
  const trimmed = value.trim();
  return trimmed ? trimmed : undefined;
};

export default function ProfileEditModal({
  isOpen,
  onClose,
  profile,
  onSaved,
}: ProfileEditModalProps) {
  const [values, setValues] = useState({
    firstName: "",
    lastName: "",
    headline: "",
    about: "",
    city: "",
    country: "",
    industry: "",
    currentCompany: "",
    currentPosition: "",
    website: "",
    githubUrl: "",
    linkedinUrl: "",
    profilePictureUrl: "",
    coverPhotoUrl: "",
    openToWork: false,
    profilePublic: true,
  });
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (!isOpen) return;

    setValues({
      firstName: profile.firstName || "",
      lastName: profile.lastName || "",
      headline: profile.headline || "",
      about: profile.about || "",
      city: profile.city || "",
      country: profile.country || "",
      industry: profile.industry || "",
      currentCompany: profile.currentCompany || "",
      currentPosition: profile.currentPosition || "",
      website: profile.website || "",
      githubUrl: profile.githubUrl || "",
      linkedinUrl: profile.linkedinUrl || "",
      profilePictureUrl: profile.profilePictureUrl || "",
      coverPhotoUrl: profile.coverPhotoUrl || "",
      openToWork: Boolean(profile.openToWork),
      profilePublic: profile.profilePublic !== false,
    });
  }, [isOpen, profile]);

  if (!isOpen) return null;

  const handleChange = (
    event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = event.target;
    setValues((previous) => ({ ...previous, [name]: value }));
  };

  const handleCheckboxChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, checked } = event.target;
    setValues((previous) => ({ ...previous, [name]: checked }));
  };

  const saveProfile = async () => {
    const payload: UpdateProfilePayload = {
      firstName: values.firstName.trim(),
      lastName: values.lastName.trim(),
      email: profile.email,
      gender: profile.gender || "PREFER_NOT_TO_SAY",
      headline: emptyToUndefined(values.headline),
      about: emptyToUndefined(values.about),
      city: emptyToUndefined(values.city),
      country: emptyToUndefined(values.country),
      industry: emptyToUndefined(values.industry),
      currentCompany: emptyToUndefined(values.currentCompany),
      currentPosition: emptyToUndefined(values.currentPosition),
      website: emptyToUndefined(values.website),
      githubUrl: emptyToUndefined(values.githubUrl),
      linkedinUrl: emptyToUndefined(values.linkedinUrl),
      profilePictureUrl: emptyToUndefined(values.profilePictureUrl),
      coverPhotoUrl: emptyToUndefined(values.coverPhotoUrl),
      openToWork: values.openToWork,
      profilePublic: values.profilePublic,
    };

    try {
      setSaving(true);
      await updateProfile(profile.id, payload);
      await onSaved();
      onClose();
    } catch (error) {
      console.error(error);
      window.alert("Unable to save the profile.");
    } finally {
      setSaving(false);
    }
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    await saveProfile();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 p-4">
      <div className="flex max-h-[90vh] w-full max-w-2xl flex-col overflow-hidden rounded-xl bg-white shadow-xl">
        <div className="flex items-center justify-between border-b border-gray-200 px-6 py-4">
          <h2 className="text-xl font-bold text-gray-900">Edit intro</h2>
          <button
            type="button"
            onClick={onClose}
            className="rounded-full p-1.5 text-gray-500 transition hover:bg-gray-100"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="flex-1 space-y-5 overflow-y-auto p-6">
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">First name*</label>
              <input
                name="firstName"
                value={values.firstName}
                onChange={handleChange}
                required
                className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">Last name*</label>
              <input
                name="lastName"
                value={values.lastName}
                onChange={handleChange}
                required
                className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Headline</label>
            <input
              name="headline"
              value={values.headline}
              onChange={handleChange}
              maxLength={220}
              className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">About</label>
            <textarea
              name="about"
              value={values.about}
              onChange={handleChange}
              rows={4}
              maxLength={5000}
              className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">City</label>
              <input
                name="city"
                value={values.city}
                onChange={handleChange}
                className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">Country</label>
              <input
                name="country"
                value={values.country}
                onChange={handleChange}
                className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">Company</label>
              <input
                name="currentCompany"
                value={values.currentCompany}
                onChange={handleChange}
                className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">Position</label>
              <input
                name="currentPosition"
                value={values.currentPosition}
                onChange={handleChange}
                className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Industry</label>
            <input
              name="industry"
              value={values.industry}
              onChange={handleChange}
              className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">Website</label>
              <input
                name="website"
                value={values.website}
                onChange={handleChange}
                placeholder="https://example.com"
                className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">LinkedIn URL</label>
              <input
                name="linkedinUrl"
                value={values.linkedinUrl}
                onChange={handleChange}
                placeholder="https://linkedin.com/in/..."
                className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">GitHub URL</label>
            <input
              name="githubUrl"
              value={values.githubUrl}
              onChange={handleChange}
              placeholder="https://github.com/..."
              className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">Profile photo URL</label>
              <input
                name="profilePictureUrl"
                value={values.profilePictureUrl}
                onChange={handleChange}
                placeholder="https://..."
                className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">Cover photo URL</label>
              <input
                name="coverPhotoUrl"
                value={values.coverPhotoUrl}
                onChange={handleChange}
                placeholder="https://..."
                className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          <div className="space-y-3">
            <label className="flex items-center gap-3 text-sm text-gray-700">
              <input
                type="checkbox"
                name="openToWork"
                checked={values.openToWork}
                onChange={handleCheckboxChange}
                className="h-4 w-4 rounded border-gray-300 text-blue-600"
              />
              Open to work
            </label>
            <label className="flex items-center gap-3 text-sm text-gray-700">
              <input
                type="checkbox"
                name="profilePublic"
                checked={values.profilePublic}
                onChange={handleCheckboxChange}
                className="h-4 w-4 rounded border-gray-300 text-blue-600"
              />
              Public profile
            </label>
          </div>
        </form>

        <div className="flex justify-end border-t border-gray-200 bg-gray-50 px-6 py-4">
          <button
            type="button"
            onClick={saveProfile}
            disabled={saving}
            className="rounded-full bg-blue-600 px-6 py-2 text-sm font-medium text-white shadow transition-colors hover:bg-blue-700 disabled:opacity-60"
          >
            {saving ? "Saving..." : "Save"}
          </button>
        </div>
      </div>
    </div>
  );
}
