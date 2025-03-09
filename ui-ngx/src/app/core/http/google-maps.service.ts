import { Injectable } from '@angular/core';
import { environment } from '@env/environment';

@Injectable({
  providedIn: 'root'
})
export class GoogleMapsService {
  private isLoaded = false;
  private loadingPromise: Promise<void> | null = null;

  loadGoogleMapsAPI(): Promise<void> {
    if (this.isLoaded) {
      return Promise.resolve();
    }

    if (this.loadingPromise) {
      return this.loadingPromise;
    }

    this.loadingPromise = new Promise<void>((resolve, reject) => {
      const script = document.createElement('script');
      script.type = 'text/javascript';
      script.src = "https://maps.googleapis.com/maps/api/js?key=AIzaSyDoEx2kaGz3PxwbI9T7ccTSg5xjdw8Nw8Q&libraries=places";
      
      script.onload = () => {
        this.isLoaded = true;
        resolve();
      };
      
      script.onerror = (error) => {
        reject(error);
      };

      document.body.appendChild(script);
    });

    return this.loadingPromise;
  }
}