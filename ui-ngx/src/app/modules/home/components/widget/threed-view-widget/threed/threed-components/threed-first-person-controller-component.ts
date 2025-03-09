

import { EventEmitter } from '@angular/core';
import * as THREE from 'three';
import { PointerLockControls } from 'three/examples/jsm/controls/PointerLockControls.js';
import { IThreedSceneManager } from "../threed-managers/ithreed-scene-manager";
import { IThreedListener } from './ithreed-listener';
import { ThreedBaseComponent } from "./threed-base-component";
import { IThreedMesh } from './ithreed-mesh';

export class ThreedFirstPersonControllerComponent extends ThreedBaseComponent implements IThreedListener, IThreedMesh {

    private readonly gravity = 9.8;
    private readonly mass = 80;
    private readonly speed = 40;
    private readonly scaleFactor = 0.1;

    private controls?: PointerLockControls;
    private velocity = new THREE.Vector3();
    private direction = new THREE.Vector3();
    private raycaster: THREE.Raycaster;

    private moveForward = false;
    private moveBackward = false;
    private moveLeft = false;
    private moveRight = false;
    private canJump = false;
    private positionMode: 'standing' | 'crouch' | 'laying' = 'standing';
    private pointerLocked = false;

    private height = 1.7;

    private prevTime = performance.now();
    public onPointerLockedChanged: EventEmitter<boolean> = new EventEmitter();

    initialize(sceneManager: IThreedSceneManager): void {
        super.initialize(sceneManager);

        const this_ = this;
        // controls
        this.controls = new PointerLockControls(this.sceneManager.camera, document.body);
        this.controls.addEventListener('lock', function () {
            this_.pointerLocked = true;
            // TODO: this_.active = true;
            this_.onPointerLockedChanged.emit(this_.pointerLocked);
        });
        this.controls.addEventListener('unlock', function () {
            this_.pointerLocked = false;
            // TODO: this_.active = false;
            this_.onPointerLockedChanged.emit(this_.pointerLocked);
        });
        this.sceneManager.scene.add(this.controls.getObject());

        this.raycaster = new THREE.Raycaster(new THREE.Vector3(), new THREE.Vector3(0, -1, 0), 0, this.height);
    }

    tick(): void {
        if (this.sceneManager.vrActive) return;

        const time = performance.now();

        if (this.controls && this.controls.isLocked === true) {

            this.raycaster.ray.origin.copy(this.controls.getObject().position);
            //this.raycaster.ray.origin.y -= 10;

            const intersections = this.raycaster.intersectObjects(this.sceneManager.scene.children, false);
            const onObject = intersections.length > 0;
            const delta = (time - this.prevTime) / 1000;

            this.velocity.x -= this.velocity.x * 10.0 * delta;
            this.velocity.z -= this.velocity.z * 10.0 * delta;

            this.velocity.y -= this.gravity * this.mass * delta * this.scaleFactor;

            this.direction.z = Number(this.moveForward) - Number(this.moveBackward);
            this.direction.x = Number(this.moveRight) - Number(this.moveLeft);
            this.direction.normalize(); // this ensures consistent movements in all directions

            if (this.moveForward || this.moveBackward) this.velocity.z -= this.direction.z * this.speed * delta;
            if (this.moveLeft || this.moveRight) this.velocity.x -= this.direction.x * this.speed * delta;

            if (onObject === true) {

                this.velocity.y = Math.max(0, this.velocity.y);
                this.canJump = true;

            }

            this.controls.moveRight(- this.velocity.x * delta);
            this.controls.moveForward(- this.velocity.z * delta);

            this.controls.getObject().position.y += (this.velocity.y * delta); // new behavior

            if (this.positionMode != 'standing' && this.controls.getObject().position.y <= this.height) {
                this.velocity.y = 0;
                this.controls.getObject().position.y = this.positionMode == 'crouch' ? this.height / 2 : this.height / 4;
                this.canJump = false;
            } else if (this.controls.getObject().position.y < this.height) {
                this.velocity.y = 0;
                this.controls.getObject().position.y = this.height;
                this.canJump = true;
            }
        }

        this.prevTime = time;

        //console.log(this.controls.getObject().position);
    }

    onKeyDown(event: KeyboardEvent): void {
        switch (event.code) {
            case 'ArrowUp':
            case 'KeyW':
                this.moveForward = true;
                break;

            case 'ArrowLeft':
            case 'KeyA':
                this.moveLeft = true;
                break;

            case 'ArrowDown':
            case 'KeyS':
                this.moveBackward = true;
                break;

            case 'ArrowRight':
            case 'KeyD':
                this.moveRight = true;
                break;

            case 'Space':
                if (this.canJump === true) this.velocity.y += 2 * this.mass * this.scaleFactor;
                this.canJump = false;
                break;

            case 'KeyP':
                console.log(this.controls.getObject().position);
                break;
        }
    }
    onKeyUp(event: KeyboardEvent): void {
        switch (event.code) {
            case 'ArrowUp':
            case 'KeyW':
                this.moveForward = false;
                break;

            case 'ArrowLeft':
            case 'KeyA':
                this.moveLeft = false;
                break;

            case 'ArrowDown':
            case 'KeyS':
                this.moveBackward = false;
                break;

            case 'ArrowRight':
            case 'KeyD':
                this.moveRight = false;
                break;

            case 'ShiftLeft':
                if (this.positionMode == 'standing') {
                    this.positionMode = 'crouch';
                } else if (this.positionMode == 'crouch') {
                    this.positionMode = 'laying';
                } else {
                    this.positionMode = 'standing';
                }
                break;
        }
    }
    onMouseMove(event: MouseEvent): void { }
    onMouseClick(event: MouseEvent): void { }

    public lockControls(): void {
        this.controls?.lock();
        this.sceneManager.mouse.set(0, 0);
    }

    getMesh(): THREE.Object3D {
        return this.controls.getObject();
    }
}