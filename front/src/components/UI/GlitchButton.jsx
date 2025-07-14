import React, { useRef, useEffect, useState } from "react";
import { gsap } from "gsap";
import "../../assets/css/GlitchButton.css";

export default function GlitchButton({
  children = "Glitch’n",
  methods,
  color = "", // "btn-login" | "btn-logout" | "btn-register"
  size = "btn-small", // 고정
}) {
  const btnRef = useRef(null);
  const turbRef = useRef(null);
  const tlRef = useRef(null);
  const [active, setActive] = useState(false);

  const idRef = useRef(`filter-${Math.random().toString(36).substr(2, 9)}`);
  const filterId = idRef.current;

  useEffect(() => {
    const feTurb = turbRef.current;
    const turbVal = { val: 1e-6 };
    const turbValX = { val: 1e-6 };

    tlRef.current = gsap
      .timeline({
        paused: true,
        repeat: -1,
        repeatDelay: 2,
        onUpdate: () => {
          feTurb.setAttribute("baseFrequency", `${turbVal.val} ${turbValX.val}`);
        },
      })
      .to(turbValX, 0.1, { val: 0.5 })
      .to(turbVal, 0.1, { val: 0.02 })
      .set(turbValX, { val: 1e-6 })
      .set(turbVal, { val: 1e-6 })
      .to(turbValX, 0.2, { val: 0.4 }, 0.4)
      .to(turbVal, 0.2, { val: 0.002 }, 0.4)
      .set(turbValX, { val: 1e-6 })
      .set(turbVal, { val: 1e-6 });

    return () => tlRef.current.kill();
  }, []);

  const handleMouseEnter = () => {
    btnRef.current.classList.add("btn-glitch-active");
    tlRef.current.restart();
  };
  const handleMouseLeave = () => {
    tlRef.current.pause();
    tlRef.current.seek(0); // 초기 위치로 리셋
    turbRef.current.setAttribute("baseFrequency", "0.000001 0.000001");
    btnRef.current.classList.remove("btn-glitch-active");
  };

  // 전역 문자열 클래스를 조합
  const classes = ["btn", size, "btn-glitch", "btn-glitch", "btn-glitch-active", color]
    .filter((c) => c !== false)
    .join(" ");

  return (
    <>
      <svg style={{ position: "absolute", width: 0, height: 0 }} aria-hidden="true">
        <defs>
          <filter id={filterId}>
            <feTurbulence
              ref={turbRef}
              type="fractalNoise"
              baseFrequency="0.000001 0.000001"
              numOctaves="1"
              result="warp"
              seed="1"
            />
            <feDisplacementMap in="SourceGraphic" in2="warp" xChannelSelector="R" yChannelSelector="G" scale="30" />
          </filter>
        </defs>
      </svg>

      <button
        ref={btnRef}
        className={classes}
        onClick={methods}
        onMouseEnter={handleMouseEnter}
        onMouseLeave={handleMouseLeave}
        style={{ filter: `url(#${filterId})` }}
      >
        {children}
      </button>
    </>
  );
}
