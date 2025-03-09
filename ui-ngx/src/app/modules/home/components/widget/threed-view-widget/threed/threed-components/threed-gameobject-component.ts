

import * as THREE from 'three';
import { GLTF } from "three/examples/jsm/loaders/GLTFLoader";
import { IThreedSceneManager } from "../threed-managers/ithreed-scene-manager";
import { IThreedMesh } from "./ithreed-mesh";
import { ThreedBaseComponent } from "./threed-base-component";
import { ThreedUtils } from '../threed-utils';

export class ThreedGameObjectComponent extends ThreedBaseComponent implements IThreedMesh {

    protected gltf: GLTF;
    protected clonedGLTF: { animations: THREE.AnimationClip[], scene: THREE.Group };
    protected mesh: THREE.Object3D;
    private addToScene: boolean;
    private customId?: string;

    constructor(mesh: THREE.Object3D | GLTF, addToScene: boolean = true, customId?: string) {
        super();
        if (mesh instanceof THREE.Object3D) {
            this.mesh = mesh;
        } else {
            this.gltf = mesh;
            this.clonedGLTF = ThreedUtils.cloneGltf(mesh);
        }
        this.customId = customId;
        this.addToScene = addToScene;
    }

    initialize(sceneManager: IThreedSceneManager): void {
        super.initialize(sceneManager);

        if (this.addToScene) {
            if (!this.mesh) {
                this.mesh = this.clonedGLTF.scene;
            }
            this.sceneManager.modelManager.replaceModel(this.mesh, { id: this.customId });
            this.sceneManager.scene.add(this.mesh);
        }
    }

    getMesh(): THREE.Object3D {
        return this.mesh;
    }
}