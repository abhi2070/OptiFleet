

import * as THREE from 'three';
import { ThreedBaseComponent } from "./threed-base-component";
import { IThreedMesh } from './ithreed-mesh';

export class ThreedAnimatorComponent extends ThreedBaseComponent {

    private readonly mesh: IThreedMesh;
    private readonly mixer: THREE.AnimationMixer;
    private readonly animations: THREE.AnimationClip[] = [];
    private readonly clock = new THREE.Clock();

    constructor(mesh: IThreedMesh, ...animations: THREE.AnimationClip[]) {
        super();

        this.mesh = mesh;
        this.animations = animations;
        this.mixer = new THREE.AnimationMixer(mesh.getMesh());
    }

    tick(): void {
        super.tick();

        this.mixer.update(this.clock.getDelta());
    }

    public play(animation: THREE.AnimationClip | string): void {
        this.unpause();

        if (animation instanceof THREE.AnimationClip) {
            this.mixer.clipAction(animation).play();
        } else {
            const anim = this.animations.find(a => a.name == animation);
            if (anim) this.mixer.clipAction(anim).play();
        }
    }

    public stop(animation?: THREE.AnimationClip | string): void {
        this.unpause();

        if (animation == undefined) {
            this.mixer.stopAllAction();
        } else if (animation instanceof THREE.AnimationClip) {
            this.mixer.clipAction(animation).stop();
        } else {
            const anim = this.animations.find(a => a.name == animation);
            if (anim) this.mixer.clipAction(anim).stop();
        }
    }

    public pause(){
        this.mixer.timeScale = 0;
    }

    public unpause(){
        this.mixer.timeScale = 1;
    }

    public isPlaying(animation: THREE.AnimationClip | string): boolean {
        if (animation instanceof THREE.AnimationClip) {
            return this.mixer.clipAction(animation).isRunning();
        } else {
            const anim = this.animations.find(a => a.name == animation);
            if (anim) return this.mixer.clipAction(anim).isRunning();
        }
        return false;
    }
}