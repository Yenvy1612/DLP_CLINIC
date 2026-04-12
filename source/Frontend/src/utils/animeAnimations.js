import anime from "animejs";

export function animatePageEnter(target, options = {}) {
    if (!target) return null;

    return anime({
        targets: target,
        opacity: [0, 1],
        translateY: [16, 0],
        duration: 320,
        easing: "easeOutCubic",
        ...options,
    });
}

export function animateModalIn({ backdrop, panel }) {
    if (!backdrop || !panel) return null;

    return anime
        .timeline({ easing: "easeOutCubic", duration: 220 })
        .add(
            {
                targets: backdrop,
                opacity: [0, 1],
            },
            0
        )
        .add(
            {
                targets: panel,
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

    const backdropAnimation = anime({
        targets: backdrop,
        opacity: [1, 0],
        duration: 160,
        easing: "easeInQuad",
    });

    const panelAnimation = anime({
        targets: panel,
        opacity: [1, 0],
        scale: [1, 0.96],
        translateY: [0, 8],
        duration: 160,
        easing: "easeInQuad",
    });

    Promise.all([backdropAnimation.finished, panelAnimation.finished])
        .then(() => onComplete?.())
        .catch(() => onComplete?.());

    return { backdropAnimation, panelAnimation };
}
