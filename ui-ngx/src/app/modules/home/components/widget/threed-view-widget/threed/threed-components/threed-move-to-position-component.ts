import * as THREE from 'three';
import { ThreedBaseComponent } from './threed-base-component';
import { IThreedListener } from './ithreed-listener';
import { EventEmitter } from '@angular/core';

export class ThreedMoveToPositionComponent extends ThreedBaseComponent implements IThreedListener {

    public onPointSelected = new EventEmitter<THREE.Vector3>();

    onKeyDown(event: KeyboardEvent): void { }
    onKeyUp(event: KeyboardEvent): void { }
    onMouseMove(event: MouseEvent): void { }
    onMouseClick(event: MouseEvent): void {
        // Calculate the mouse position in normalized device coordinates (NDC)
        const mouse = this.sceneManager.mouse;

        // Create a ray from the camera through the mouse position
        const raycaster = new THREE.Raycaster();
        raycaster.setFromCamera(this.getRaycasterOriginCoords(), this.sceneManager.camera);

        // Find the intersection between the ray and the scene
        const intersects = raycaster.intersectObjects(this.sceneManager.scene.children, true);

        // If there's an intersection, return the point in world coordinates
        if (intersects.length > 0) {
            this.onPointSelected.emit(intersects[0].point);
        }
    }

    protected getRaycasterOriginCoords(): THREE.Vector2 {
        return new THREE.Vector2(this.sceneManager.mouse.x, this.sceneManager.mouse.y);
    }
}
