import keycloak from "../features/auth/keycloak";

export default function LoginPage() {
  const login = () =>
    keycloak.login({
      redirectUri: `${window.location.origin}/`,
    });

  const register = () =>
    keycloak.register({
      redirectUri: `${window.location.origin}/`,
    });

  return (
    <div className="min-h-screen bg-white">
      {/* Header */}
      <header className="flex justify-between items-center px-10 py-6">
        <div className="text-4xl font-bold text-[#0A66C2]">
          Linked
          <span className="bg-[#0A66C2] text-white px-1 rounded">
            in
          </span>
        </div>

        <div className="flex gap-4">
          <button
            onClick={register}
            className="text-gray-700 font-semibold hover:text-black"
          >
            Join now
          </button>

          <button
            onClick={login}
            className="border border-[#0A66C2] text-[#0A66C2] px-5 py-2 rounded-full font-semibold hover:bg-blue-50"
          >
            Sign in
          </button>
        </div>
      </header>

      {/* Main Section */}
      <div className="max-w-6xl mx-auto px-10 pt-10">
        <div className="grid md:grid-cols-2 gap-12 items-center">

          {/* Left Side */}
          <div>
            <h1 className="text-5xl md:text-6xl font-light text-[#8F5849] leading-tight mb-10">
              Welcome to your professional community
            </h1>

            <div className="w-full max-w-md">
              <button
                onClick={login}
                className="w-full bg-[#0A66C2] text-white py-3 rounded-full font-semibold hover:bg-[#004182]"
              >
                Sign in with Keycloak
              </button>

              <div className="flex items-center my-6">
                <div className="border-t flex-1"></div>
                <span className="px-4 text-gray-500">or</span>
                <div className="border-t flex-1"></div>
              </div>

              <button
                onClick={register}
                className="w-full border border-gray-500 py-3 rounded-full font-semibold hover:bg-gray-100"
              >
                Create account
              </button>

              <p className="text-xs text-gray-500 mt-6 text-center">
                By clicking Continue, you agree to LinkedIn's User Agreement,
                Privacy Policy and Cookie Policy.
              </p>
            </div>
          </div>

          {/* Right Side Illustration */}
          <div className="hidden md:flex justify-center">
            <div className="relative w-[500px] h-[450px]">

              {/* Background Circle */}
              <div className="absolute w-80 h-80 bg-[#EEF3F8] rounded-full top-10 left-16"></div>

              {/* User Card */}
              <div className="absolute top-16 left-0 bg-white rounded-2xl shadow-xl p-5 w-64">
                <div className="flex items-center gap-3">
                  <div className="w-12 h-12 rounded-full bg-[#0A66C2]"></div>

                  <div>
                    <div className="h-3 w-24 bg-gray-300 rounded mb-2"></div>
                    <div className="h-2 w-16 bg-gray-200 rounded"></div>
                  </div>
                </div>

                <div className="mt-4">
                  <div className="h-2 bg-gray-200 rounded mb-2"></div>
                  <div className="h-2 bg-gray-200 rounded w-3/4"></div>
                </div>
              </div>

              {/* Post Card */}
              <div className="absolute top-32 right-0 bg-white rounded-2xl shadow-xl p-5 w-72">
                <div className="h-3 bg-gray-300 rounded w-32 mb-4"></div>

                <div className="space-y-2">
                  <div className="h-2 bg-gray-200 rounded"></div>
                  <div className="h-2 bg-gray-200 rounded"></div>
                  <div className="h-2 bg-gray-200 rounded w-2/3"></div>
                </div>
              </div>

              {/* Jobs Card */}
              <div className="absolute bottom-10 left-24 bg-white rounded-2xl shadow-xl p-5 w-60">
                <div className="text-5xl mb-2">💼</div>

                <h3 className="font-semibold text-gray-800">
                  Find opportunities
                </h3>

                <p className="text-sm text-gray-500 mt-2">
                  Connect with professionals, discover jobs,
                  and grow your career.
                </p>
              </div>

            </div>
          </div>

        </div>
      </div>
    </div>
  );
}