

import { ElementRef } from '@angular/core';
import { IThreedSceneManager } from "../threed-managers/ithreed-scene-manager";
import { IThreedProgress } from './ithreed-progress';
import { ThreedBaseComponent } from "./threed-base-component";


export class ThreedProgressBarComponent extends ThreedBaseComponent implements IThreedProgress {

    private static lastId: number = 0;

    private html: string;
    private containerId: string;
    private progressBarId: string;
    private progressBarContainer?: HTMLElement;
    private progressBarElement?: HTMLElement;
    private visible: boolean;

    initialize(sceneManager: IThreedSceneManager): void {
        super.initialize(sceneManager);

        this.generateHtmlAndId();
        const s = this.sceneManager.onRendererContainerChange.subscribe(container => {
            this.attachHtmlToContainer(container);
        });
        this.subscriptions.push(s);
    }

    private generateHtmlAndId(): void {
        const id = ThreedProgressBarComponent.lastId++;
        this.containerId = `Container_ThreedProgressBarComponentId_${id}`;
        this.progressBarId = `Element_ThreedProgressBarComponentId_${id}`;
        this.html = `
        <div id="${this.containerId}" 
            style="position: absolute; top:0px!important; width: 100%; height: 4px; background-color: #f5f5f5; border-radius: 2px;">
            <div id="${this.progressBarId}"
                style="position: absolute; top: 0; left: 0; height: 100%; background-color: #007bff; border-radius: 2px; transition: width 0.1s ease-in-out;" 
                role="progressbar" 
                aria-valuenow="0" 
                aria-valuemin="0" 
                aria-valuemax="100">
            </div>
        </div>
        `;
    }

    private attachHtmlToContainer(container: ElementRef) {
        if (this.progressBarContainer) this.progressBarContainer.remove();
        container.nativeElement.insertAdjacentHTML('afterbegin', this.html);
        setTimeout(() => {
            this.tryGetElements(container);
        }, 0);
    }

    public updateProgress(progress: number) {
        progress *= 100;
        if (progress >= 100) this.hide();
        else this.show();

        //console.log("updateProgress " + progress, this.progressBarElement == undefined);

        if (this.progressBarElement) {
            this.progressBarElement.style.width = `${progress}%`;
            this.progressBarElement.setAttribute('aria-valuenow', progress.toString());
        } else this.tryGetElements();
    }

    private hide() {
        if (this.progressBarContainer && this.visible) {
            this.progressBarContainer.style.display = 'none';
            this.visible = false;
        }
    }

    private show() {
        if (this.progressBarContainer && !this.visible) {
            this.progressBarContainer.style.display = 'block';
            this.visible = true;
        }
    }

    private tryGetElements(container?: ElementRef) {
        this.progressBarContainer = container?.nativeElement.querySelector(`#${this.containerId}`) || document.getElementById(this.containerId);
        this.progressBarElement = container?.nativeElement.querySelector(`#${this.progressBarId}`) || document.getElementById(this.progressBarId);
        this.visible = true;
        this.hide();
    }
}