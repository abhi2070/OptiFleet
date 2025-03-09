

export interface IThreedListener {
    onKeyDown(event: KeyboardEvent): void;
    onKeyUp(event: KeyboardEvent): void;
    onMouseMove(event: MouseEvent): void;
    onMouseClick(event: MouseEvent): void;
}