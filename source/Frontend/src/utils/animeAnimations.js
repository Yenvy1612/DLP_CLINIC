import { animate, createTimeline } from "animejs";

export function animatePageEnter(target, options = {}) {
    if (!target) return null;

    return animate(target, {
        opacity: [0, 1],
        translateY: [16, 0],
        duration: 320,
        easing: "easeOutCubic",
        ...options,
    });
}

export function animateModalIn({ backdrop, panel }) {
    if (!backdrop || !panel) return null;

    return createTimeline({ easing: "easeOutCubic", duration: 220 })
        .add(
            backdrop,
            {
                opacity: [0, 1],
            },
            0
        )
        .add(
            panel,
            {
                opacity: [0, 1],
                scale: [0.96, 1],
                translateY: [14, 0],
            },
            0
        );
}

export function animateModalOut({ backdrop, panel, onComplete }) {
    if (!backdrop || !panel) {
        onComplete?.();
        return null;
    }

    const backdropAnimation = animate(backdrop, {
        opacity: [1, 0],
        duration: 160,
        easing: "easeInQuad",
    });

    const panelAnimation = animate(panel, {
        opacity: [1, 0],
        scale: [1, 0.96],
        translateY: [0, 8],
        duration: 160,
        easing: "easeInQuad",
    });

    Promise.all([backdropAnimation, panelAnimation])
        .then(() => onComplete?.())
        .catch(() => onComplete?.());

    return { backdropAnimation, panelAnimation };
}
