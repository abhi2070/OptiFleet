/* eslint-disable max-len */
/* eslint-disable @typescript-eslint/no-shadow */
/* eslint-disable @typescript-eslint/no-non-null-assertion */
import { EventEmitter } from '@angular/core';
import * as THREE from 'three';
import { GLTF } from 'three/examples/jsm/loaders/GLTFLoader';
import { OBJECT_ID_TAG, ROOT_TAG } from '../threed-constants';
import { ThreedVectorSettings } from '../threed-models';
import { ThreedUtils } from '../threed-utils';
import { IThreedSceneManager } from './ithreed-scene-manager';

export interface ModelData {
    id: string;
    gltf: GLTF | THREE.Object3D;
    root: THREE.Object3D;
    flatModel: THREE.Object3D[];
    explodedModel?: THREE.Group;
}

export interface ModelConfig {
    id?: string;
    autoResize?: boolean;
}

const defaultModelConfig: ModelConfig = { autoResize: false };

export class ThreedModelManager {

    public models: Map<string, ModelData> = new Map();
    private sceneManager: IThreedSceneManager;
    private flatModels: THREE.Object3D[] = [];

    public onAfterAddModel = new EventEmitter<ModelData>();
    public onBeforeRemoveModel = new EventEmitter<ModelData>();

    constructor(sceneManager: IThreedSceneManager) {
        this.sceneManager = sceneManager;
    }

    public getAllFlatModels(): THREE.Object3D[] {
        return this.flatModels;
    }

    public replaceModel(model: GLTF | THREE.Object3D, configs: ModelConfig = defaultModelConfig): void {
        const uuid = model instanceof THREE.Object3D ? model.uuid : model.scene.uuid;
        this.removeModel(configs?.id || uuid);
        this.addModel(model, configs);
    }

    protected addModel(model: GLTF | THREE.Object3D, configs: ModelConfig = defaultModelConfig): void {
        const root = model instanceof THREE.Object3D ? model : model.scene;
        const customId = configs.id || root.uuid;
        root.userData[OBJECT_ID_TAG] = customId;
        root.userData[ROOT_TAG] = true;
        const flatModel: THREE.Object3D[] = [];
        root.traverse(o => {
            if (o instanceof THREE.Mesh) {flatModel.push(o);}
        });
        const modelData: ModelData = { id: customId, gltf: model, root, flatModel };
        this.models.set(customId, modelData);

        if (configs.autoResize) {
            const distance = this.sceneManager.camera.position.distanceTo(new THREE.Vector3());
            ThreedUtils.autoScaleModel(model, Math.floor(distance));
        }

        if (this.sceneManager.configs?.shadow) {
            root.traverse(object => {
                if (object instanceof THREE.Mesh) {
                    object.castShadow = true;
                    object.receiveShadow = true;
                }
            });
        }

        this.sceneManager.scene!.add(root);

        this.onAfterAddModel.emit(modelData);
        this.recalculateFlatModels();
    }

    public removeModel(id: string): void {
        if (!this.models.has(id)) {return;}

        const modelData = this.models.get(id);
        this.onBeforeRemoveModel.emit(modelData);

        const parent = modelData!.root.parent;
        parent.remove(modelData!.root);
        this.models.delete(id);

        this.recalculateFlatModels();
    }

    private recalculateFlatModels() {
        this.flatModels = [];
        for (const modelData of this.models.values()) {
            this.flatModels.push(...modelData.flatModel);
        }
    }

    public explodeObjectByDistance(id: string, distance: number) {
        if (!this.models.has(id)) {return;}

        const modelData = this.models.get(id);
        const object = modelData!.root;
        if (!modelData!.explodedModel) {
            const explodedModel = ThreedUtils.splitIntoMeshes(object);
            const box = new THREE.Box3().setFromObject(explodedModel);
            explodedModel.userData.defaultCenterPosition = box.getCenter(new THREE.Vector3());
            this.sceneManager.scene.add(explodedModel);

            modelData!.explodedModel = explodedModel;
        }

        const explodedModel = modelData!.explodedModel;
        if (distance === 0) {
            object.visible = true;
            explodedModel!.visible = false;
            return;
        } else {
            object.visible = false;
            explodedModel!.visible = true;
        }

        explodedModel!.updateMatrixWorld(); // make sure object's world matrix is up to date

        const center = explodedModel!.userData.defaultCenterPosition;
        // Move each mesh away from the object's center along a radial direction
        explodedModel!.children.forEach((mesh: THREE.Object3D) => {
            const position = mesh.userData.defaultPosition.clone();
            const centerPosition = mesh.userData.defaultCenterPosition.clone();
            const direction = centerPosition.clone().sub(center);
            const offset = direction.multiplyScalar(distance);
            mesh.position.copy(position.add(offset));
        });
    }

    public updateModelTransforms(id: string, settings: {
        threedPositionVectorSettings?: ThreedVectorSettings;
        threedRotationVectorSettings?: ThreedVectorSettings;
        threedScaleVectorSettings?: ThreedVectorSettings;
    }) {

        const model = this.models.get(id);
        if (!model) {return;}

        const position = settings.threedPositionVectorSettings;
        const rotation = settings.threedRotationVectorSettings;
        const scale = settings.threedScaleVectorSettings;

        if (position) {model.root.position.set(position.x, position.y, position.z);}
        if (rotation) {model.root.rotation.set(THREE.MathUtils.degToRad(rotation.x), THREE.MathUtils.degToRad(rotation.y), THREE.MathUtils.degToRad(rotation.z));}
        if (scale) {model.root.scale.set(scale.x, scale.y, scale.z);}
    }

    public onDestroy() {}
}
