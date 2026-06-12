export default function RightNewsCard() {
  return (
    <aside className="space-y-4">
      <div className="bg-white rounded-lg shadow p-4">
        <h3 className="font-semibold mb-3">LinkedIn News</h3>

        <ul className="space-y-3 text-sm">
          <li>
            <p className="font-medium">Java jobs continue to grow</p>
            <p className="text-gray-500">2h ago · 1,240 readers</p>
          </li>
          <li>
            <p className="font-medium">Spring Boot microservices in demand</p>
            <p className="text-gray-500">4h ago · 980 readers</p>
          </li>
          <li>
            <p className="font-medium">Kubernetes skills rising</p>
            <p className="text-gray-500">1d ago · 2,540 readers</p>
          </li>
        </ul>
      </div>

      <div className="bg-white rounded-lg shadow p-4 text-sm text-gray-500">
        About · Accessibility · Help Center · Privacy · Terms
      </div>
    </aside>
  );
}