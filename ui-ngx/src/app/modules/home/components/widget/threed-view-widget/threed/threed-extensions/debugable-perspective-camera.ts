

import * as THREE from 'three';
import { PerspectiveCamera } from "three";
import { IThreedSceneManager } from "../threed-managers/ithreed-scene-manager";
import { ThreedWebRenderer } from "../threed-managers/threed-web-renderer";

export class DebugablePerspectiveCamera {

    public camera: PerspectiveCamera;
    private sceneManager: IThreedSceneManager;
    private renderer: THREE.WebGLRenderer;
    private cameraHelper: THREE.CameraHelper;

    private debugCameraScreenWidth: number = 1;
    private debugCameraScreenHeight: number = 1;
    private cameraPreviewScale: number = 4;
    quad: THREE.Mesh<THREE.PlaneGeometry, THREE.ShaderMaterial>;

    constructor(sceneManager: IThreedSceneManager, cameraPreviewScale = 4, camera?: PerspectiveCamera) {
        this.sceneManager = sceneManager;
        this.cameraPreviewScale = cameraPreviewScale;
        this.renderer = sceneManager.getTRenderer(ThreedWebRenderer).getRenderer();
        this.camera = camera || new THREE.PerspectiveCamera(60, 1, 0.1, 1);
        this.cameraHelper = new THREE.CameraHelper(this.camera);

        this.sceneManager.scene.add(this.cameraHelper);

        this.addRedFilter();
        this.onResize();
    }

    private addRedFilter() {
        // Create a full-screen quad
        const geometry = new THREE.PlaneGeometry(2, 2);
        const material = new THREE.ShaderMaterial({
          uniforms: {
            color: { value: new THREE.Color(1, 0, 0) },
            opacity: { value: 0.5 }
          },
          vertexShader: `
            void main() {
              gl_Position = vec4(position, 1.0);
            }
          `,
          fragmentShader: `
            uniform vec3 color;
            uniform float opacity;
      
            void main() {
              gl_FragColor = vec4(color, opacity);
            }
          `,
          transparent: true
        });
        this.quad = new THREE.Mesh(geometry, material);
        this.quad.position.z = -1;
        this.sceneManager.scene.add(this.quad);
    }

    public onResize(): void {
        this.debugCameraScreenWidth = this.sceneManager.screenWidth / this.cameraPreviewScale;
        this.debugCameraScreenHeight = this.sceneManager.screenHeight / this.cameraPreviewScale;
        this.camera.aspect = 1//this.debugCameraScreenWidth / this.debugCameraScreenHeight;
        this.camera.updateProjectionMatrix();
    }

    public preview(sensed = false) {
        this.onResize();

        this.quad.visible = sensed;
        const originalViewport = this.renderer.getViewport(new THREE.Vector4());
        this.cameraHelper.visible = false;

        const x = this.sceneManager.screenWidth - this.debugCameraScreenWidth;
        this.renderer.clearDepth();
        this.renderer.setScissorTest(true);
        this.renderer.setScissor(x, 0, this.debugCameraScreenWidth, this.debugCameraScreenHeight)
        this.renderer.setViewport(x, 0, this.debugCameraScreenWidth, this.debugCameraScreenHeight);
        this.renderer.render(this.sceneManager.scene, this.camera);
        this.renderer.setScissorTest(false);
        this.renderer.setViewport(originalViewport);

        this.cameraHelper.visible = true;
        this.quad.visible = false;
        this.cameraHelper.update();
    }
}