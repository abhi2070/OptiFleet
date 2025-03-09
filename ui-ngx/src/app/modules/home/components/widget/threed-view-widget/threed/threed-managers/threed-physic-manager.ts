import * as THREE from 'three';
import * as CANNON from 'cannon-es';
import { IThreedPhysic } from '../threed-components/ithreed-physic';
import { IThreedSceneManager } from './ithreed-scene-manager';
import { IThreedPhysicObject } from '../threed-components/ithreed-physic-object';

export class ThreedPhysicManager implements IThreedPhysic {

    public readonly world: CANNON.World;

    private readonly sceneManager: IThreedSceneManager;
    private readonly clock = new THREE.Clock();
    public components: IThreedPhysicObject[] = [];

    constructor(sceneManager: IThreedSceneManager) {
        this.sceneManager = sceneManager;
        this.world = new CANNON.World();
        this.world.gravity.set(0, 0, 0);

        this.world.addEventListener('beginContact', (event) => this.onBeginContact(event));
        this.world.addEventListener('endContact', (event) => this.onEndContact(event));
    }

    private onBeginContact(event: CANNON.Constraint) {
        if (this.components.length <= 1) {return;}

        let componentA: IThreedPhysicObject | undefined;
        let componentB: IThreedPhysicObject | undefined;
        for (const component of this.components) {
            if (event.bodyA.id === component.physicBody.id) {
                componentA = component;
            } else if (event.bodyB.id === component.physicBody.id) {
                componentB = component;
            }
            if (componentA && componentB) {break;}
        }

        componentA?.onBeginCollision.emit({ event, object: componentB });
        componentB?.onBeginCollision.emit({ event, object: componentA });
    }

    private onEndContact(event: CANNON.Constraint) {
        if (this.components.length <= 1) {return;}

        let componentA: IThreedPhysicObject | undefined;
        let componentB: IThreedPhysicObject | undefined;
        for (const component of this.components) {
            if (event.bodyA.id === component.physicBody.id) {
                componentA = component;
            } else if (event.bodyB.id === component.physicBody.id) {
                componentB = component;
            }

            if (componentA && componentB) {break;}
        }

        componentA?.onEndCollision.emit({ event, object: componentB });
        componentB?.onEndCollision.emit({ event, object: componentA });
    }

    public addPhysic(component: IThreedPhysicObject): void {
        this.components.push(component);
        this.world.addBody(component.physicBody);
    }

    public removePhysic(component: IThreedPhysicObject): void {
        for (let i = 0; i < this.components.length; i++) {
            const c = this.components[i];
            if (c === component) {
                this.world.removeBody(component.physicBody);
                this.components.splice(i, 1);
                return;
            }
        }
    }

    public setVisualiseColliders(visualiseBodies: boolean): void {
        this.components.forEach(c => c.setVisualiseColliders(visualiseBodies));
    }

    beforeUpdatePhysics(): void {
        this.components.forEach(c => c.beforeUpdatePhysics());
    }

    updatePhysics(): void {
        this.beforeUpdatePhysics();
        //const delta = this.clock.getDelta();
        this.world.fixedStep();
        //this.world.step(delta);
        this.components.forEach(c => c.updatePhysics());
    }

    updateVisuals(): void {
        this.components.forEach(c => c.updateVisuals());
    }


}
