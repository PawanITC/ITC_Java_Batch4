import React from 'react';
import { X } from 'lucide-react';
import { useFormik } from 'formik';
import * as Yup from 'yup';

interface EducationFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  initialData: any;
}

export default function EducationFormModal({ isOpen, onClose, initialData }: EducationFormModalProps) {
  


  // Validation Rules
  const validationSchema = Yup.object().shape({
    schoolName: Yup.string().trim().required("School name is required"),
    degree: Yup.string().trim().required("Degree is required"),
    fieldOfStudy: Yup.string().trim().required("Field of study is required"),
    startYear: Yup.number().required("Start year is required"),
    endYear: Yup.number()
      .required("End year is required")
      .min(Yup.ref('startYear'), "End year cannot be earlier than start year"),
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
    validationSchema: validationSchema,
    onSubmit: (values, { resetForm }) => {
      const payload = {
        ...(initialData?.id && { id: initialData.id }),
        schoolName: values.schoolName,
        degree: values.degree,
        fieldOfStudy: values.fieldOfStudy,
        // Ensure values are sent to your Java backend as Numbers
        startYear: Number(values.startYear),
        endYear: Number(values.endYear),
      };

      if (initialData?.id) {
        console.log("Sending PUT Request to update education:", payload);
        // axios.put(`.../api/education/${initialData.id}`, payload)...
      } else {
        console.log("Sending POST Request to add new education:", payload);
        // axios.post('.../api/education', payload)...
      }

      resetForm();
      onClose();
    },
  });
  
  if (!isOpen) return null;
  // Generates array of years for selection options (e.g., 2026 down to 1990)
  const years = Array.from({ length: 37 }, (_, i) => String(2026 - i));

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 p-4">
      <div className="w-full max-w-2xl bg-white rounded-xl shadow-xl flex flex-col overflow-hidden max-h-[90vh]">
        
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-200">
          <h2 className="text-xl font-bold text-gray-900">
            {initialData ? "Edit education" : "Add education"}
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
        <form onSubmit={formik.handleSubmit} className="p-6 overflow-y-auto space-y-5 flex-1">
          
          {/* School Field */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">School*</label>
            <input
              type="text"
              name="schoolName"
              placeholder="Ex: Boston University"
              value={formik.values.schoolName}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              className={`w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 ${
                formik.touched.schoolName && formik.errors.schoolName 
                  ? "border-red-500 focus:ring-red-200" 
                  : "border-gray-300 focus:ring-blue-500"
              }`}
            />
            {formik.touched.schoolName && formik.errors.schoolName && (
              <p className="text-red-500 text-xs mt-1">{formik.errors.schoolName as any}</p>
            )}
          </div>

          {/* Degree Field */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Degree*</label>
            <input
              type="text"
              name="degree"
              placeholder="Ex: Bachelor of Science"
              value={formik.values.degree}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              className={`w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 ${
                formik.touched.degree && formik.errors.degree 
                  ? "border-red-500 focus:ring-red-200" 
                  : "border-gray-300 focus:ring-blue-500"
              }`}
            />
            {formik.touched.degree && formik.errors.degree && (
              <p className="text-red-500 text-xs mt-1">{formik.errors.degree as any}</p>
            )}
          </div>

          {/* Field of Study */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Field of study*</label>
            <input
              type="text"
              name="fieldOfStudy"
              placeholder="Ex: Business"
              value={formik.values.fieldOfStudy}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              className={`w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 ${
                formik.touched.fieldOfStudy && formik.errors.fieldOfStudy 
                  ? "border-red-500 focus:ring-red-200" 
                  : "border-gray-300 focus:ring-blue-500"
              }`}
            />
            {formik.touched.fieldOfStudy && formik.errors.fieldOfStudy && (
              <p className="text-red-500 text-xs mt-1">{formik.errors.fieldOfStudy as any}</p>
            )}
          </div>

          {/* Year Range Fields */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Start year*</label>
              <select
                name="startYear"
                value={formik.values.startYear}
                onChange={formik.handleChange}
                onBlur={formik.handleBlur}
                className={`w-full border rounded-lg px-3 py-2 text-sm bg-white focus:outline-none focus:ring-2 ${
                  formik.touched.startYear && formik.errors.startYear 
                    ? "border-red-500 focus:ring-red-200" 
                    : "border-gray-300 focus:ring-blue-500"
                }`}
              >
                {years.map((yr) => <option key={yr} value={yr}>{yr}</option>)}
              </select>
              {formik.touched.startYear && formik.errors.startYear && (
                <p className="text-red-500 text-xs mt-1">{formik.errors.startYear as any}</p>
              )}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">End year (or expected)*</label>
              <select
                name="endYear"
                value={formik.values.endYear}
                onChange={formik.handleChange}
                onBlur={formik.handleBlur}
                className={`w-full border rounded-lg px-3 py-2 text-sm bg-white focus:outline-none focus:ring-2 ${
                  formik.touched.endYear && formik.errors.endYear 
                    ? "border-red-500 focus:ring-red-200" 
                    : "border-gray-300 focus:ring-blue-500"
                }`}
              >
                {years.map((yr) => <option key={yr} value={yr}>{yr}</option>)}
              </select>
              {formik.touched.endYear && formik.errors.endYear && (
                <p className="text-red-500 text-xs mt-1">{formik.errors.endYear as any}</p>
              )}
            </div>
          </div>

        </form>

        {/* Footer Action Buttons */}
        <div className="px-6 py-4 border-t border-gray-200 bg-gray-50 flex justify-end">
          <button
            type="submit"
            onClick={() => formik.handleSubmit()}
            className="px-6 py-2 bg-blue-600 hover:bg-blue-700 text-white font-medium text-sm rounded-full shadow transition-colors"
          >
            Save
          </button>
        </div>

      </div>
    </div>
  );
}