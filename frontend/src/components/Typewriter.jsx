import { useEffect, useState } from 'react';

/**
 * Typewriter component renders text character‑by‑character.
 * Props:
 *  - text: string to display
 *  - speed: ms per character (default 80)
 *  - className: optional CSS classes
 */
export default function Typewriter({ text = '', speed = 80, className = '' }) {
  const [displayed, setDisplayed] = useState('');

  useEffect(() => {
    let i = 0;
    const timer = setInterval(() => {
      setDisplayed(text.slice(0, ++i));
      if (i === text.length) clearInterval(timer);
    }, speed);
    return () => clearInterval(timer);
  }, [text, speed]);

  return (
    <h1 className={`${className} typewriter-cursor`} style={{ fontSize: 'clamp(40px, 6vw, 72px)', fontWeight: 800, lineHeight: 1.08, marginBottom: '24px', letterSpacing: '-0.03em' }}>
      {displayed}
    </h1>
  );
}
