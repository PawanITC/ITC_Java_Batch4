import React from 'react';
import { Formik, Form, Field, FieldArray, ErrorMessage } from 'formik';
import * as Yup from 'yup';
import { X, Plus, Trash2 } from 'lucide-react';
import { postNewJob } from './jobPostingApi';

// --- Validation Schema using Yup ---
const JobPostSchema = Yup.object().shape({
  companyId: Yup.string().uuid('Invalid UUID').required('Company ID is required'),
  title: Yup.string().required('Job title is required'),
  description: Yup.string().required('Job description is required'),
  location: Yup.string().required('Location is required'),
  salaryMin: Yup.number().positive('Must be positive').required('Minimum salary is required'),
  salaryMax: Yup.number().positive('Must be positive').min(Yup.ref('salaryMin'), 'Max salary must be greater than min salary').required('Maximum salary is required'),
  status: Yup.string().oneOf(['OPEN', 'CLOSED', 'DRAFT'], 'Invalid status').required('Status is required'),
  requirements: Yup.array().of(
    Yup.object().shape({
      requirement: Yup.string().required('Requirement text is required'),
      isMandatory: Yup.boolean()
    })
  ).min(1, 'At least one requirement is required'),
  benefits: Yup.array().of(Yup.string().required('Benefit text cannot be empty')).min(1, 'At least one benefit is required')
});

interface JobPostModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmitSuccess: () => void;
}

export default function JobPostModal({ isOpen, onClose, onSubmitSuccess }: JobPostModalProps) {
  if (!isOpen) return null;

  const initialValues = {
    companyId: '9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d', 
    title: '',
    description: '',
    location: '',
    salaryMin: '',
    salaryMax: '',
    status: 'OPEN',
    requirements: [{ requirement: '', isMandatory: true }],
    benefits: ['']
  };

  const handleSubmit = async (values: any, { setSubmitting, resetForm }: any) => {
  try {
    // Run clean client interface request handling
    const result = await postNewJob(values);
    console.log('Job saved:', result);
    
    alert('Job posted successfully!');
    resetForm();
    if (onSubmitSuccess) onSubmitSuccess();
    onClose();
  } catch (error) {
    console.error('API Post Request Failed:', error);
    alert('Failed to save your job posting.');
  } finally {
    setSubmitting(false);
  }
};

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 overflow-y-auto p-4">
      <div className="bg-white rounded-xl shadow-xl w-full max-w-2xl max-h-[90vh] overflow-y-auto flex flex-col">
        
        <div className="flex justify-between items-center px-6 py-4 border-b border-gray-200 sticky top-0 bg-white z-10">
          <h2 className="text-xl font-semibold text-gray-900">Post a new job</h2>
          <button onClick={onClose} className="p-1 rounded-full hover:bg-gray-100 text-gray-500">
            <X size={20} />
          </button>
        </div>

        <Formik
          initialValues={initialValues}
          validationSchema={JobPostSchema}
          onSubmit={handleSubmit}
        >
          {({ values, isSubmitting }) => (
            <Form className="p-6 space-y-5 flex-1">

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-gray-600 mb-1">Job Title</label>
                  <Field name="title" placeholder="e.g. Junior Developer" className="w-full p-2 border border-gray-300 rounded-md focus:ring-1 focus:ring-blue-500 outline-none text-sm" />
                  <ErrorMessage name="title" component="div" className="text-red-500 text-xs mt-1" />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-gray-600 mb-1">Location</label>
                  <Field name="location" placeholder="e.g. San Francisco, CA (Remote)" className="w-full p-2 border border-gray-300 rounded-md focus:ring-1 focus:ring-blue-500 outline-none text-sm" />
                  <ErrorMessage name="location" component="div" className="text-red-500 text-xs mt-1" />
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-gray-600 mb-1">Min Salary</label>
                  <Field type="number" name="salaryMin" className="w-full p-2 border border-gray-300 rounded-md focus:ring-1 focus:ring-blue-500 outline-none text-sm" />
                  <ErrorMessage name="salaryMin" component="div" className="text-red-500 text-xs mt-1" />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-gray-600 mb-1">Max Salary</label>
                  <Field type="number" name="salaryMax" className="w-full p-2 border border-gray-300 rounded-md focus:ring-1 focus:ring-blue-500 outline-none text-sm" />
                  <ErrorMessage name="salaryMax" component="div" className="text-red-500 text-xs mt-1" />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-gray-600 mb-1">Job Status</label>
                  <Field as="select" name="status" className="w-full p-2 border border-gray-300 rounded-md bg-white focus:ring-1 focus:ring-blue-500 outline-none text-sm">
                    <option value="OPEN">OPEN</option>
                    <option value="DRAFT">DRAFT</option>
                    <option value="CLOSED">CLOSED</option>
                  </Field>
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-600 mb-1">Description</label>
                <Field as="textarea" name="description" rows="3" className="w-full p-2 border border-gray-300 rounded-md focus:ring-1 focus:ring-blue-500 outline-none text-sm" />
                <ErrorMessage name="description" component="div" className="text-red-500 text-xs mt-1" />
              </div>

              <hr className="border-gray-200" />

              <div>
                <label className="block text-sm font-semibold text-gray-900 mb-2">Requirements</label>
                <FieldArray name="requirements">
                  {({ push, remove }) => (
                    <div className="space-y-3">
                      {values.requirements.map((_, index) => (
                        <div key={index} className="flex gap-2 items-start bg-gray-50 p-2.5 rounded-lg border border-gray-200">
                          <div className="flex-1 space-y-2">
                            <Field name={`requirements.${index}.requirement`} placeholder="Add a critical skill or experience requirement..." className="w-full p-2 border border-gray-300 rounded-md bg-white text-sm outline-none" />
                            <ErrorMessage name={`requirements.${index}.requirement`} component="div" className="text-red-500 text-xs" />
                            
                            <label className="flex items-center gap-1.5 text-xs text-gray-600 cursor-pointer">
                              <Field type="checkbox" name={`requirements.${index}.isMandatory`} className="rounded text-blue-600" />
                              Is Mandatory requirement
                            </label>
                          </div>
                          {values.requirements.length > 1 && (
                            <button type="button" onClick={() => remove(index)} className="text-red-500 hover:bg-red-50 p-1.5 rounded mt-1">
                              <Trash2 size={16} />
                            </button>
                          )}
                        </div>
                      ))}
                      <button type="button" onClick={() => push({ requirement: '', isMandatory: true })} className="inline-flex items-center gap-1 text-xs font-semibold text-blue-600 hover:underline mt-1">
                        <Plus size={14} /> Add Requirement
                      </button>
                    </div>
                  )}
                </FieldArray>
              </div>

              <hr className="border-gray-200" />

              <div>
                <label className="block text-sm font-semibold text-gray-900 mb-2">Benefits & Perks</label>
                <FieldArray name="benefits">
                  {({ push, remove }) => (
                    <div className="space-y-2">
                      {values.benefits.map((_, index) => (
                        <div key={index} className="flex items-center gap-2">
                          <div className="flex-1">
                            <Field name={`benefits.${index}`} placeholder="e.g. 100% Remote Flexibility" className="w-full p-2 border border-gray-300 rounded-md text-sm outline-none" />
                            <ErrorMessage name={`benefits.${index}`} component="div" className="text-red-500 text-xs mt-0.5" />
                          </div>
                          {values.benefits.length > 1 && (
                            <button type="button" onClick={() => remove(index)} className="text-red-500 hover:bg-red-50 p-1.5 rounded">
                              <Trash2 size={16} />
                            </button>
                          )}
                        </div>
                      ))}
                      <button type="button" onClick={() => push('')} className="inline-flex items-center gap-1 text-xs font-semibold text-blue-600 hover:underline mt-1">
                        <Plus size={14} /> Add Benefit
                      </button>
                    </div>
                  )}
                </FieldArray>
              </div>

              <div className="pt-4 border-t border-gray-200 flex justify-end gap-3 sticky bottom-0 bg-white">
                <button type="button" onClick={onClose} className="px-4 py-1.5 border border-gray-300 rounded-full text-sm font-semibold text-gray-700 hover:bg-gray-50">
                  Cancel
                </button>
                <button type="submit" disabled={isSubmitting} className="px-5 py-1.5 bg-blue-600 hover:bg-blue-700 text-white rounded-full text-sm font-semibold disabled:opacity-50">
                  {isSubmitting ? 'Posting...' : 'Post Job'}
                </button>
              </div>
            </Form>
          )}
        </Formik>
      </div>
    </div>
  );
}