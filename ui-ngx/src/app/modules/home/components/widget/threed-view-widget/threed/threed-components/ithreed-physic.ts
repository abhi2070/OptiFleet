

export interface IThreedPhysic {
    beforeUpdatePhysics(): void;
    updatePhysics(): void;
    updateVisuals(): void;

    setVisualiseColliders(visualiseColliders: boolean): void;
}