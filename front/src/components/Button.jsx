import React from "react";
import styles from "../assets/css/technical/technicalButton.module.scss";

const TButton = ({
  children,
  variant = "dark", // 'dark' | 'light' | 'clear' | 'glitch'
  glitchActive = false,
  size = "", // ""       | "small"
  className: customClass = "", // 부모가 넘긴 className
  ...rest
}) => {
  // 1) CSS Module 해시 클래스부터
  const classes = [styles.btn];

  // 2) variant 분기
  if (variant === "light") classes.push(styles["btn-light"]);
  else if (variant === "clear") classes.push(styles["btn-clear"]);
  else if (variant === "glitch") {
    classes.push(styles["btn-glitch"]);
    if (glitchActive) classes.push(styles["btn-glitch-active"]);
  } else classes.push(styles["btn-dark"]);

  // 3) size 분기
  if (size === "small") classes.push(styles["btn-small"]);

  // 4) 부모가 넘긴 커스텀 클래스 (필요 없으면 지워도 됩니다)
  if (customClass) classes.push(customClass);

  return (
    // ⚠️ rest 안에 className 이 빠졌기 때문에,
    //     외부에서 넘긴 className 이 여기에 남아 있지 않습니다.
    <button {...rest} className={classes.join(" ")}>
      {children}
    </button>
  );
};

export default TButton;
