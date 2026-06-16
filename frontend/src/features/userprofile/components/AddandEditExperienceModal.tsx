import { X } from "lucide-react";
import { useFormik } from "formik";
import * as Yup from "yup";

export default function AddandEditExperienceModal({ isOpen, onClose, initialData }: any) {
  
  // Helper to safely pull Year (YYYY) and Month (MM) parts out of database raw string strings ("YYYY-MM-DD")
  const parseDatePart = (dateStr: string, part: "month" | "year") => {
    if (!dateStr) return part === "month" ? "06" : "2026";
    const segments = dateStr.split("-"); // ["2024", "01", "01"]
    return part === "year" ? segments[0] : segments[1];
  };

  const validationSchema = Yup.object().shape({
    title: Yup.string().required("Job title is required"),
    companyName: Yup.string().required("Organization name is required"),
    location: Yup.string().required("Location is required"),
    locationType: Yup.string().required("Please select a location type"),
    employmentType: Yup.string().required("Please select an employment type"),
    current: Yup.boolean(),
    startMonth: Yup.string().required("Start month is required"),
    startYear: Yup.string().required("Start year is required"),
    endMonth: Yup.string().when("current", {
      is: false,
      then: (schema) => schema.required("End month is required"),
      otherwise: (schema) => schema.notRequired(),
    }),
    endYear: Yup.string().when("current", {
      is: false,
      then: (schema) => schema.required("End year is required"),
      otherwise: (schema) => schema.notRequired(),
    }),
    description: Yup.string().required("Description is required"),
  });

  const formik = useFormik({
    // Enable Formik to dynamically re-populate fields whenever initialData changes status
    enableReinitialize: true, 
    initialValues: {
      title: initialData?.title || "",
      companyName: initialData?.companyName || "",
      location: initialData?.location || "Crawley", // Fallback matching screenshot structure context layout placeholders
      locationType: initialData?.locationType || "On-site",
      employmentType: initialData?.employmentType || "Full-time",
      current: initialData?.current || false,
      startMonth: parseDatePart(initialData?.startDate, "month"),
      startYear: parseDatePart(initialData?.startDate, "year"),
      endMonth: parseDatePart(initialData?.endDate, "month"),
      endYear: parseDatePart(initialData?.endDate, "year"),
      description: initialData?.description || "",
    },
    validationSchema: validationSchema,
    onSubmit: (values, { resetForm }) => {
      const formattedStartDate = `${values.startYear}-${values.startMonth}-01`;
      const formattedEndDate = values.current ? null : `${values.endYear}-${values.endMonth}-01`;

      const payload = {
        ...(initialData?.id && { id: initialData.id }), // Include matching existing ID if editing
        title: values.title,
        companyName: values.companyName,
        location: values.location,
        locationType: values.locationType,
        employmentType: values.employmentType,
        startDate: formattedStartDate,
        endDate: formattedEndDate,
        current: values.current,
        description: values.description,
      };

      if (initialData?.id) {
        console.log("Sending PUT request to update existing entry payload:", payload);
        // Your fetch PUT api call route goes here...
      } else {
        console.log("Sending POST request to append fresh role payload:", payload);
        // Your fetch POST api call route goes here...
      }

      resetForm();
      onClose();
    },
  });

  if (!isOpen) return null;

  const months = [
    { value: "01", name: "January" }, { value: "02", name: "February" },
    { value: "03", name: "March" }, { value: "04", name: "April" },
    { value: "05", name: "May" }, { value: "06", name: "June" },
    { value: "07", name: "July" }, { value: "08", name: "August" },
    { value: "09", name: "September" }, { value: "10", name: "October" },
    { value: "11", name: "November" }, { value: "12", name: "December" },
  ];
  const years = Array.from({ length: 30 }, (_, i) => String(2026 - i));

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 p-4">
      <div className="w-full max-w-2xl bg-white rounded-xl shadow-xl flex flex-col overflow-hidden max-h-[90vh]">
        
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-200">
          <h2 className="text-xl font-bold text-gray-900">
            {initialData ? "Edit role details" : "Add a role to your profile"}
          </h2>
          <button 
            type="button" 
            onClick={() => { formik.resetForm(); onClose(); }} 
            className="p-1.5 hover:bg-gray-100 rounded-full text-gray-500 transition"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Scrollable Form Body */}
        <form onSubmit={formik.handleSubmit} className="p-6 overflow-y-auto space-y-5 flex-1">
          <h3 className="text-lg font-semibold text-gray-900">Let's start with the basics</h3>

          {/* Job Title & Organization */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Job title*</label>
              <input
                type="text"
                name="title"
                placeholder="Example: Senior Product Manager"
                value={formik.values.title}
                onChange={formik.handleChange}
                onBlur={formik.handleBlur}
                className={`w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 ${
                  formik.touched.title && formik.errors.title ? "border-red-500 focus:ring-red-200" : "border-gray-300 focus:ring-blue-500"
                }`}
              />
              {formik.touched.title && formik.errors.title && <p className="text-red-500 text-xs mt-1">{formik.errors.title as any}</p>}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Organization*</label>
              <input
                type="text"
                name="companyName"
                placeholder="Example: Microsoft"
                value={formik.values.companyName}
                onChange={formik.handleChange}
                onBlur={formik.handleBlur}
                className={`w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 ${
                  formik.touched.companyName && formik.errors.companyName ? "border-red-500 focus:ring-red-200" : "border-gray-300 focus:ring-blue-500"
                }`}
              />
              {formik.touched.companyName && formik.errors.companyName && <p className="text-red-500 text-xs mt-1">{formik.errors.companyName as any}</p>}
            </div>
          </div>

        

          

          {/* Current Working Checkbox */}
          <div className="flex items-center gap-2 py-1">
            <input
              type="checkbox"
              id="current"
              name="current"
              checked={formik.values.current}
              onChange={formik.handleChange}
              className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
            />
            <label htmlFor="current" className="text-sm text-gray-700 select-none">
              I currently work here
            </label>
          </div>

          {/* Date Picker Blocks */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 pt-1">
            <div className="grid grid-cols-2 gap-2">
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Start month</label>
                <select
                  name="startMonth"
                  value={formik.values.startMonth}
                  onChange={formik.handleChange}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm bg-white focus:outline-none"
                >
                  {months.map((m) => <option key={m.value} value={m.value}>{m.name}</option>)}
                </select>
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Start year*</label>
                <select
                  name="startYear"
                  value={formik.values.startYear}
                  onChange={formik.handleChange}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm bg-white focus:outline-none"
                >
                  {years.map((y) => <option key={y} value={y}>{y}</option>)}
                </select>
              </div>
            </div>

            {!formik.values.current ? (
              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="block text-xs font-medium text-gray-600 mb-1">End month</label>
                  <select
                    name="endMonth"
                    value={formik.values.endMonth}
                    onChange={formik.handleChange}
                    className={`w-full border rounded-lg px-3 py-2 text-sm bg-white focus:outline-none ${
                      formik.touched.endMonth && formik.errors.endMonth ? "border-red-500" : "border-gray-300"
                    }`}
                  >
                    {months.map((m) => <option key={m.value} value={m.value}>{m.name}</option>)}
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-600 mb-1">End year*</label>
                  <select
                    name="endYear"
                    value={formik.values.endYear}
                    onChange={formik.handleChange}
                    className={`w-full border rounded-lg px-3 py-2 text-sm bg-white focus:outline-none ${
                      formik.touched.endYear && formik.errors.endYear ? "border-red-500" : "border-gray-300"
                    }`}
                  >
                    {years.map((y) => <option key={y} value={y}>{y}</option>)}
                  </select>
                </div>
              </div>
            ) : (
              <div className="flex items-end pb-1.5">
                <span className="text-xs font-semibold text-emerald-700 bg-emerald-50 px-3 py-2 rounded-lg border border-emerald-200">
                  Present Active Role
                </span>
              </div>
            )}
          </div>

          {/* Description */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Description*</label>
            <textarea
              rows={4}
              name="description"
              placeholder="Describe your role..."
              value={formik.values.description}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              className={`w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 ${
                formik.touched.description && formik.errors.description ? "border-red-500 focus:ring-red-200" : "border-gray-300 focus:ring-blue-500"
              }`}
            />
            {formik.touched.description && formik.errors.description && <p className="text-red-500 text-xs mt-1">{formik.errors.description as any}</p>}
          </div>
        </form>

        {/* Footer Action Buttons */}
        <div className="px-6 py-4 border-t border-gray-200 bg-gray-50 flex justify-end">
          <button
            type="button"
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