

import { EventEmitter } from "@angular/core";

export class ThreedEventManager {

    private static _instance: ThreedEventManager;
    public static get instance(): ThreedEventManager {
        if (!this._instance) this._instance = new ThreedEventManager();
        return this._instance;
    }

    public onMouseMove: EventEmitter<MouseEvent> = new EventEmitter();
    public onMouseClick: EventEmitter<MouseEvent> = new EventEmitter();
    public onKeyDown: EventEmitter<KeyboardEvent> = new EventEmitter();
    public onKeyUp: EventEmitter<KeyboardEvent> = new EventEmitter();

    private constructor() {
        this.setupEventListeners();
    }

    private setupEventListeners() {
        window.addEventListener('mousemove', (event: MouseEvent) => this.onMouseMove.emit(event));
        window.addEventListener('click', (event: MouseEvent) => this.onMouseClick.emit(event));
        window.addEventListener('keydown', (event: KeyboardEvent) => this.onKeyDown.emit(event));
        window.addEventListener('keyup', (event: KeyboardEvent) => this.onKeyUp.emit(event));
    }
}