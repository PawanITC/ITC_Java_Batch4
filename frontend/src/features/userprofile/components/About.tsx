import React, { useState } from "react";
import {
  Pencil,
  Gem,
  ArrowRight,
} from "lucide-react";
import SkillsModal from "./SkillsModal";

function About() {
  const [showMore, setShowMore] = useState(false);
  const [openModal, setOpenModal] = useState(false);

  return (
    <>
      <div className="bg-white rounded-xl border p-8">
        <div className="flex justify-between items-start">
          <h2 className="text-3xl font-bold">
            About
          </h2>

          <Pencil
            size={22}
            className="cursor-pointer"
          />
        </div>

        <div className="mt-8 text-md leading-relaxed">
          <p>
            👋 I'm a passionate MERN stack developer
            with 3 years of hands-on experience
            building responsive, scalable web
            applications. I specialize in developing
            full-stack solutions using MongoDB,
            Express.js, React, and Node.js, with a
            focus on performance, clean code, and
            user experience.
          </p>

          {showMore && (
            <div className="mt-4 space-y-4">
              <p>
                I enjoy solving real-world problems
                through code and love working in agile
                teams that value collaboration,
                learning, and shipping great products.
                Whether it's optimizing backend APIs
                or building sleek frontend interfaces,
                I thrive on creating meaningful
                digital experiences.
              </p>

              <p>
                🔍 Currently open to new opportunities
                where I can grow, contribute to
                impactful projects, and continue
                learning from experienced developers.
              </p>

              <p>Let's connect!</p>
            </div>
          )}

          <button
            className="text-gray-500 mt-3 text-xl"
            onClick={() => setShowMore(!showMore)}
          >
            {showMore ? "Show less" : "... more"}
          </button>
        </div>

        {/* Skills Card */}
        <div className="border rounded-2xl p-8 mt-8">
          <div className="flex justify-between items-center">
            <div className="flex gap-5">
              <Gem size={40} />

              <div>
                <h3 className="font-bold text-2xl">
                  Top skills
                </h3>

                <p className="text-xl mt-1">
                  React.js • Node.js • JavaScript •
                  Next.js • Express.js
                </p>
              </div>
            </div>

            <ArrowRight
              size={25}
              className="cursor-pointer"
              onClick={() => setOpenModal(true)}
            />
          </div>
        </div>
      </div>

      <SkillsModal
        isOpen={openModal}
        onClose={() => setOpenModal(false)}
      />
    </>
  );
}

export default About;