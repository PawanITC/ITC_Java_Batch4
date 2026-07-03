import { X } from "lucide-react";
import { useFormik } from "formik";
import * as Yup from "yup";
import { createExperience, updateExperience } from "../api";

interface ExperienceModalProps {
  isOpen: boolean;
  onClose: () => void;
  initialData: any;
  profileId: string;
  onSaved: () => Promise<void>;
}

export default function AddandEditExperienceModal({
  isOpen,
  onClose,
  initialData,
  profileId,
  onSaved,
}: ExperienceModalProps) {
  const parseDatePart = (dateStr: string, part: "month" | "year") => {
    if (!dateStr) return part === "month" ? "06" : "2026";
    const segments = dateStr.split("-");
    return part === "year" ? segments[0] : segments[1];
  };

  const validationSchema = Yup.object().shape({
    title: Yup.string().required("Job title is required"),
    companyName: Yup.string().required("Organization name is required"),
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
    enableReinitialize: true,
    initialValues: {
      title: initialData?.title || "",
      companyName: initialData?.companyName || "",
      current: initialData?.current || false,
      startMonth: parseDatePart(initialData?.startDate, "month"),
      startYear: parseDatePart(initialData?.startDate, "year"),
      endMonth: parseDatePart(initialData?.endDate, "month"),
      endYear: parseDatePart(initialData?.endDate, "year"),
      description: initialData?.description || "",
    },
    validationSchema,
    onSubmit: async (values, { resetForm, setSubmitting }) => {
      const formattedStartDate = `${values.startYear}-${values.startMonth}-01`;
      const formattedEndDate = values.current ? null : `${values.endYear}-${values.endMonth}-01`;

      const payload = {
        profileId,
        title: values.title,
        companyName: values.companyName,
        startDate: formattedStartDate,
        endDate: formattedEndDate,
        current: values.current,
        description: values.description,
      };

      try {
        if (initialData?.id) {
          await updateExperience(initialData.id, payload);
        } else {
          await createExperience(payload);
        }

        await onSaved();
        resetForm();
        onClose();
      } catch (error) {
        console.error(error);
        window.alert("Unable to save the experience entry.");
      } finally {
        setSubmitting(false);
      }
    },
  });

  if (!isOpen) return null;

  const months = [
    { value: "01", name: "January" },
    { value: "02", name: "February" },
    { value: "03", name: "March" },
    { value: "04", name: "April" },
    { value: "05", name: "May" },
    { value: "06", name: "June" },
    { value: "07", name: "July" },
    { value: "08", name: "August" },
    { value: "09", name: "September" },
    { value: "10", name: "October" },
    { value: "11", name: "November" },
    { value: "12", name: "December" },
  ];
  const years = Array.from({ length: 30 }, (_, index) => String(2026 - index));

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 p-4">
      <div className="flex max-h-[90vh] w-full max-w-2xl flex-col overflow-hidden rounded-xl bg-white shadow-xl">
        <div className="flex items-center justify-between border-b border-gray-200 px-6 py-4">
          <h2 className="text-xl font-bold text-gray-900">
            {initialData ? "Edit role details" : "Add a role to your profile"}
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
          <h3 className="text-lg font-semibold text-gray-900">Let's start with the basics</h3>

          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">Job title*</label>
              <input
                type="text"
                name="title"
                placeholder="Example: Senior Product Manager"
                value={formik.values.title}
                onChange={formik.handleChange}
                onBlur={formik.handleBlur}
                className={`w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 ${
                  formik.touched.title && formik.errors.title
                    ? "border-red-500 focus:ring-red-200"
                    : "border-gray-300 focus:ring-blue-500"
                }`}
              />
              {formik.touched.title && formik.errors.title && (
                <p className="mt-1 text-xs text-red-500">{formik.errors.title as any}</p>
              )}
            </div>

            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">Organization*</label>
              <input
                type="text"
                name="companyName"
                placeholder="Example: Microsoft"
                value={formik.values.companyName}
                onChange={formik.handleChange}
                onBlur={formik.handleBlur}
                className={`w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 ${
                  formik.touched.companyName && formik.errors.companyName
                    ? "border-red-500 focus:ring-red-200"
                    : "border-gray-300 focus:ring-blue-500"
                }`}
              />
              {formik.touched.companyName && formik.errors.companyName && (
                <p className="mt-1 text-xs text-red-500">{formik.errors.companyName as any}</p>
              )}
            </div>
          </div>

          <div className="flex items-center gap-2 py-1">
            <input
              type="checkbox"
              id="current"
              name="current"
              checked={formik.values.current}
              onChange={formik.handleChange}
              className="h-4 w-4 rounded border-gray-300 text-blue-600 focus:ring-blue-500"
            />
            <label htmlFor="current" className="select-none text-sm text-gray-700">
              I currently work here
            </label>
          </div>

          <div className="grid grid-cols-1 gap-4 pt-1 md:grid-cols-2">
            <div className="grid grid-cols-2 gap-2">
              <div>
                <label className="mb-1 block text-xs font-medium text-gray-600">Start month</label>
                <select
                  name="startMonth"
                  value={formik.values.startMonth}
                  onChange={formik.handleChange}
                  className="w-full rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm focus:outline-none"
                >
                  {months.map((month) => (
                    <option key={month.value} value={month.value}>
                      {month.name}
                    </option>
                  ))}
                </select>
              </div>
              <div>
                <label className="mb-1 block text-xs font-medium text-gray-600">Start year*</label>
                <select
                  name="startYear"
                  value={formik.values.startYear}
                  onChange={formik.handleChange}
                  className="w-full rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm focus:outline-none"
                >
                  {years.map((year) => (
                    <option key={year} value={year}>
                      {year}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {!formik.values.current ? (
              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="mb-1 block text-xs font-medium text-gray-600">End month</label>
                  <select
                    name="endMonth"
                    value={formik.values.endMonth}
                    onChange={formik.handleChange}
                    className={`w-full rounded-lg border px-3 py-2 text-sm focus:outline-none ${
                      formik.touched.endMonth && formik.errors.endMonth
                        ? "border-red-500"
                        : "border-gray-300"
                    }`}
                  >
                    {months.map((month) => (
                      <option key={month.value} value={month.value}>
                        {month.name}
                      </option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="mb-1 block text-xs font-medium text-gray-600">End year*</label>
                  <select
                    name="endYear"
                    value={formik.values.endYear}
                    onChange={formik.handleChange}
                    className={`w-full rounded-lg border px-3 py-2 text-sm focus:outline-none ${
                      formik.touched.endYear && formik.errors.endYear
                        ? "border-red-500"
                        : "border-gray-300"
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
            ) : (
              <div className="flex items-end pb-1.5">
                <span className="rounded-lg border border-emerald-200 bg-emerald-50 px-3 py-2 text-xs font-semibold text-emerald-700">
                  Present active role
                </span>
              </div>
            )}
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Description*</label>
            <textarea
              rows={4}
              name="description"
              placeholder="Describe your role..."
              value={formik.values.description}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              className={`w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 ${
                formik.touched.description && formik.errors.description
                  ? "border-red-500 focus:ring-red-200"
                  : "border-gray-300 focus:ring-blue-500"
              }`}
            />
            {formik.touched.description && formik.errors.description && (
              <p className="mt-1 text-xs text-red-500">{formik.errors.description as any}</p>
            )}
          </div>
        </form>

        <div className="flex justify-end border-t border-gray-200 bg-gray-50 px-6 py-4">
          <button
            type="button"
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
