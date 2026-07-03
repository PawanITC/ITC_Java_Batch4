import React from "react";
import { X } from "lucide-react";
import { useFormik } from "formik";
import * as Yup from "yup";
import { createEducation, updateEducation } from "../api";

interface EducationFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  initialData: any;
  profileId?: string;
  onSaved: () => Promise<void>;
}

export default function EducationFormModal({
  isOpen,
  onClose,
  initialData,
  profileId,
  onSaved,
}: EducationFormModalProps) {
  const validationSchema = Yup.object().shape({
    schoolName: Yup.string().trim().required("School name is required"),
    degree: Yup.string().trim().required("Degree is required"),
    fieldOfStudy: Yup.string().trim().required("Field of study is required"),
    startYear: Yup.number().required("Start year is required"),
    endYear: Yup.number()
      .required("End year is required")
      .min(Yup.ref("startYear"), "End year cannot be earlier than start year"),
  });

  const formik = useFormik({
    enableReinitialize: true,
    initialValues: {
      schoolName: initialData?.schoolName || "",
      degree: initialData?.degree || "",
      fieldOfStudy: initialData?.fieldOfStudy || "",
      startYear: initialData?.startYear || "2026",
      endYear: initialData?.endYear || "2026",
    },
    validationSchema,
    onSubmit: async (values, { resetForm, setSubmitting }) => {
      if (!profileId) {
        window.alert("No profile is available for this education entry.");
        setSubmitting(false);
        return;
      }

      const payload = {
        profileId,
        schoolName: values.schoolName,
        degree: values.degree,
        fieldOfStudy: values.fieldOfStudy,
        startYear: Number(values.startYear),
        endYear: Number(values.endYear),
      };

      try {
        if (initialData?.id) {
          await updateEducation(initialData.id, payload);
        } else {
          await createEducation(payload);
        }

        await onSaved();
        resetForm();
        onClose();
      } catch (error) {
        console.error(error);
        window.alert("Unable to save the education entry.");
      } finally {
        setSubmitting(false);
      }
    },
  });

  if (!isOpen) return null;

  const years = Array.from({ length: 37 }, (_, index) => String(2026 - index));

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 p-4">
      <div className="flex max-h-[90vh] w-full max-w-2xl flex-col overflow-hidden rounded-xl bg-white shadow-xl">
        <div className="flex items-center justify-between border-b border-gray-200 px-6 py-4">
          <h2 className="text-xl font-bold text-gray-900">
            {initialData ? "Edit education" : "Add education"}
          </h2>
          <button
            type="button"
            onClick={() => {
              formik.resetForm();
              onClose();
            }}
            className="rounded-full p-1.5 text-gray-500 transition hover:bg-gray-100"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        <form onSubmit={formik.handleSubmit} className="flex-1 space-y-5 overflow-y-auto p-6">
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">School*</label>
            <input
              type="text"
              name="schoolName"
              placeholder="Ex: Boston University"
              value={formik.values.schoolName}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              className={`w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 ${
                formik.touched.schoolName && formik.errors.schoolName
                  ? "border-red-500 focus:ring-red-200"
                  : "border-gray-300 focus:ring-blue-500"
              }`}
            />
            {formik.touched.schoolName && formik.errors.schoolName && (
              <p className="mt-1 text-xs text-red-500">{formik.errors.schoolName as any}</p>
            )}
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Degree*</label>
            <input
              type="text"
              name="degree"
              placeholder="Ex: Bachelor of Science"
              value={formik.values.degree}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              className={`w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 ${
                formik.touched.degree && formik.errors.degree
                  ? "border-red-500 focus:ring-red-200"
                  : "border-gray-300 focus:ring-blue-500"
              }`}
            />
            {formik.touched.degree && formik.errors.degree && (
              <p className="mt-1 text-xs text-red-500">{formik.errors.degree as any}</p>
            )}
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Field of study*</label>
            <input
              type="text"
              name="fieldOfStudy"
              placeholder="Ex: Business"
              value={formik.values.fieldOfStudy}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              className={`w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 ${
                formik.touched.fieldOfStudy && formik.errors.fieldOfStudy
                  ? "border-red-500 focus:ring-red-200"
                  : "border-gray-300 focus:ring-blue-500"
              }`}
            />
            {formik.touched.fieldOfStudy && formik.errors.fieldOfStudy && (
              <p className="mt-1 text-xs text-red-500">{formik.errors.fieldOfStudy as any}</p>
            )}
          </div>

          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">Start year*</label>
              <select
                name="startYear"
                value={formik.values.startYear}
                onChange={formik.handleChange}
                onBlur={formik.handleBlur}
                className={`w-full rounded-lg border bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 ${
                  formik.touched.startYear && formik.errors.startYear
                    ? "border-red-500 focus:ring-red-200"
                    : "border-gray-300 focus:ring-blue-500"
                }`}
              >
                {years.map((year) => (
                  <option key={year} value={year}>
                    {year}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">
                End year (or expected)*
              </label>
              <select
                name="endYear"
                value={formik.values.endYear}
                onChange={formik.handleChange}
                onBlur={formik.handleBlur}
                className={`w-full rounded-lg border bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 ${
                  formik.touched.endYear && formik.errors.endYear
                    ? "border-red-500 focus:ring-red-200"
                    : "border-gray-300 focus:ring-blue-500"
                }`}
              >
                {years.map((year) => (
                  <option key={year} value={year}>
                    {year}
                  </option>
                ))}
              </select>
            </div>
          </div>
        </form>

        <div className="flex justify-end border-t border-gray-200 bg-gray-50 px-6 py-4">
          <button
            type="submit"
            onClick={() => formik.handleSubmit()}
            disabled={formik.isSubmitting}
            className="rounded-full bg-blue-600 px-6 py-2 text-sm font-medium text-white shadow transition-colors hover:bg-blue-700"
          >
            {formik.isSubmitting ? "Saving..." : "Save"}
          </button>
        </div>
      </div>
    </div>
  );
}
