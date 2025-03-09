/* eslint-disable max-len */
import * as CANNON from 'cannon-es';
import * as THREE from 'three';
import { IThreedSceneManager } from '../threed-managers/ithreed-scene-manager';
import { ThreedGameObjectComponent } from './threed-gameobject-component';
import { ThreedRigidbodyComponent } from './threed-rigidbody-component';
import { threeToCannon } from 'three-to-cannon';

export class ThreedGroupGameObjectComponent extends ThreedGameObjectComponent {

    public readonly gameobjects: ThreedGameObjectComponent[] = [];
    public readonly rigidbodies: ThreedRigidbodyComponent[] = [];

    constructor(mesh: THREE.Object3D) {
        super(mesh);
    }

    public initialize(sceneManager: IThreedSceneManager) {
        super.initialize(sceneManager);

        const previousContraints = new Map<string, CANNON.Body[]>();
        this.mesh.traverse(o => {
            if (o.userData.physicShape) {
                // create rigidbody for o

                const t = new THREE.Vector3();
                o.getWorldPosition(t);

                const gameobject = new ThreedGameObjectComponent(o, false);

                const result = threeToCannon(o as any, o.userData.physicShape);
                const { shape, offset, orientation } = result;
                const mass = o.userData.mass ?? 10;
                const pb = new CANNON.Body({ mass, material: new CANNON.Material({ restitution: 0, friction: 1 }), type: CANNON.BODY_TYPES.DYNAMIC, linearDamping: 0.1, angularDamping: 0.1 });
                pb.addShape(shape, offset, orientation);
                pb.position.set(t.x, t.y, t.z);
                pb.allowSleep = true;


                const joints: CANNON.Constraint[] = [];
                if (o.userData.constraintLockTag !== undefined) {
                    const lockContraintName = o.userData.constraintLockTag;
                    let prevs = [];
                    if (previousContraints.has(lockContraintName)) {
                        prevs = previousContraints.get(lockContraintName);
                        prevs.forEach(p => {
                            joints.push(new CANNON.LockConstraint(pb, p));
                        });
                    }
                    prevs.push(pb);
                    previousContraints.set(lockContraintName, prevs);
                }
                const rigidbody = new ThreedRigidbodyComponent({ mesh: gameobject, physicBody: pb, joints });


                this.gameobjects.push(gameobject);
                this.rigidbodies.push(rigidbody);

                this.sceneManager.add(gameobject, true);
                this.sceneManager.add(rigidbody, true);
            }
        });
    }
}
