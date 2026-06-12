import keycloak from "../features/auth/keycloak";

export default function LoginPage() {
  const login = () =>
  keycloak.login({
    redirectUri: `${window.location.origin}/`,
  });

const register = () =>
  keycloak.register({
    redirectUri: `${window.location.origin}/user-profile`,
  });

  return (
    <div className="min-h-screen bg-white">
      <header className="flex justify-between items-center px-10 py-6">
        <div className="text-4xl font-bold text-[#0A66C2]">
          Linked<span className="bg-[#0A66C2] text-white px-1 rounded">in</span>
        </div>

        <div className="flex gap-4">
          <button onClick={register} className="text-gray-700 font-semibold hover:text-black">
            Join now
          </button>

          <button onClick={login} className="border border-[#0A66C2] text-[#0A66C2] px-5 py-2 rounded-full font-semibold hover:bg-blue-50">
            Sign in
          </button>
        </div>
      </header>

      <div className="max-w-7xl mx-auto px-10 pt-10">
        <div className="grid md:grid-cols-2 gap-10 items-center">
          <div>
            <h1 className="text-6xl font-light text-[#8F5849] leading-tight mb-10">
              Welcome to your professional community
            </h1>

            <div className="w-full max-w-md">
              <button onClick={login} className="w-full bg-[#0A66C2] text-white py-3 rounded-full font-semibold hover:bg-[#004182]">
                Sign in with Keycloak
              </button>

              <div className="flex items-center my-6">
                <div className="border-t flex-1"></div>
                <span className="px-4 text-gray-500">or</span>
                <div className="border-t flex-1"></div>
              </div>

              <button onClick={register} className="w-full border border-gray-500 py-3 rounded-full font-semibold hover:bg-gray-100">
                Create account
              </button>

              <p className="text-xs text-gray-500 mt-6 text-center">
                By clicking Continue, you agree to LinkedIn's User Agreement,
                Privacy Policy and Cookie Policy.
              </p>
            </div>
          </div>

          <div className="hidden md:flex justify-center">
            <img
              src="https://media.licdn.com/media/AAYQAgSuAAgAAQAAAAAAAK3KJrM6T4i0Q6m1T6S3Z4h0RQ.png"
              alt="LinkedIn"
              className="max-w-full h-auto"
            />
          </div>
        </div>
      </div>
    </div>
  );
}