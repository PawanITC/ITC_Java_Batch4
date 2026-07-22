import React from "react";
import { X } from "lucide-react";
import { useFormik } from "formik";
import * as Yup from "yup";
import { createSkill, updateSkill } from "../api";

interface SkillFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  initialData: any; // expects { id: string, skillName: string } or null
  profileId: string;
  onSaved: () => Promise<void>;
}

export default function SkillFormModal({
  isOpen,
  onClose,
  initialData,
  profileId,
  onSaved,
}: SkillFormModalProps) {
  const validationSchema = Yup.object().shape({
    skillName: Yup.string()
      .trim()
      .required("Skill name is required"),
  });

  const formik = useFormik({
    enableReinitialize: true,
    initialValues: {
      skillName: initialData?.skillName || "",
    },
    validationSchema: validationSchema,
    onSubmit: async (values, { resetForm, setSubmitting }) => {
      const payload = {
        profileId,
        skillName: values.skillName,
        endorsementCount: initialData?.endorsementCount || 0,
      };

      try {
        if (initialData?.id) {
          await updateSkill(initialData.id, payload);
        } else {
          await createSkill(payload);
        }

        await onSaved();
        resetForm();
        onClose();
      } catch (error) {
        console.error(error);
        window.alert("Unable to save the skill.");
      } finally {
        setSubmitting(false);
      }
    },
  });

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-[60] flex items-center justify-center bg-black bg-opacity-50 p-4">
      <div className="w-full max-w-md bg-white rounded-xl shadow-xl flex flex-col overflow-hidden">
        
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-200">
          <h2 className="text-lg font-bold text-gray-900">
            {initialData ? "Edit skill" : "Add skill"}
          </h2>
          <button 
            type="button" 
            onClick={() => { formik.resetForm(); onClose(); }} 
            className="p-1.5 hover:bg-gray-100 rounded-full text-gray-500 transition"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Form Body */}
        <form onSubmit={formik.handleSubmit} className="p-6 space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Skill name*
            </label>
            <input
              type="text"
              name="skillName"
              placeholder="Example: React, Java, Axios"
              value={formik.values.skillName}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              className={`w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 ${
                formik.touched.skillName && formik.errors.skillName 
                  ? "border-red-500 focus:ring-red-200" 
                  : "border-gray-300 focus:ring-blue-500"
              }`}
            />
            {formik.touched.skillName && formik.errors.skillName && (
              <p className="text-red-500 text-xs mt-1">{formik.errors.skillName as any}</p>
            )}
          </div>
        </form>

        {/* Action Footer */}
        <div className="px-6 py-4 border-t border-gray-200 bg-gray-50 flex justify-end gap-2">
          <button
            type="button"
            onClick={() => { formik.resetForm(); onClose(); }}
            className="px-4 py-2 border border-gray-300 rounded-full text-sm font-medium text-gray-700 hover:bg-gray-100 transition-colors"
          >
            Cancel
          </button>
          <button
            type="submit"
            onClick={() => formik.handleSubmit()}
            disabled={formik.isSubmitting}
            className="px-6 py-2 bg-blue-600 hover:bg-blue-700 text-white font-medium text-sm rounded-full shadow transition-colors"
          >
            {formik.isSubmitting ? "Saving..." : "Save"}
          </button>
        </div>

      </div>
    </div>
  );
}
