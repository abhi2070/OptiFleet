

import { Injectable } from '@angular/core';
import { defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import {
  AdminSettings,
  AutoCommitSettings,
  MailConfigTemplate,
  FeaturesInfo,
  JwtSettings,
  MailServerSettings,
  RepositorySettings,
  RepositorySettingsInfo,
  SecuritySettings,
  TestSmsRequest,
  UpdateMessage,
  RegistrationSettings
} from '@shared/models/settings.models';
import { EntitiesVersionControlService } from '@core/http/entities-version-control.service';
import { tap } from 'rxjs/operators';
import { LoginResponse } from '@shared/models/login.models';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  constructor(
    private http: HttpClient,
    private entitiesVersionControlService: EntitiesVersionControlService
  ) { }

  public getAdminSettings<T>(key: string, config?: RequestConfig): Observable<AdminSettings<T>> {
    return this.http.get<AdminSettings<T>>(`/api/admin/settings/${key}`, defaultHttpOptionsFromConfig(config));
  }

  public saveAdminSettings<T>(adminSettings: AdminSettings<T>,
    config?: RequestConfig): Observable<AdminSettings<T>> {
    return this.http.post<AdminSettings<T>>('/api/admin/settings', adminSettings, defaultHttpOptionsFromConfig(config));
  }

  public sendTestMail(adminSettings: AdminSettings<MailServerSettings>,
    config?: RequestConfig): Observable<void> {
    return this.http.post<void>('/api/admin/settings/testMail', adminSettings, defaultHttpOptionsFromConfig(config));
  }

  public sendTestSms(testSmsRequest: TestSmsRequest,
    config?: RequestConfig): Observable<void> {
    return this.http.post<void>('/api/admin/settings/testSms', testSmsRequest, defaultHttpOptionsFromConfig(config));
  }

  public getSecuritySettings(config?: RequestConfig): Observable<SecuritySettings> {
    return this.http.get<SecuritySettings>(`/api/admin/securitySettings`, defaultHttpOptionsFromConfig(config));
  }

  public saveSecuritySettings(securitySettings: SecuritySettings,
    config?: RequestConfig): Observable<SecuritySettings> {
    return this.http.post<SecuritySettings>('/api/admin/securitySettings', securitySettings,
      defaultHttpOptionsFromConfig(config));
  }

  public getJwtSettings(config?: RequestConfig): Observable<JwtSettings> {
    return this.http.get<JwtSettings>(`/api/admin/jwtSettings`, defaultHttpOptionsFromConfig(config));
  }

  public saveJwtSettings(jwtSettings: JwtSettings, config?: RequestConfig): Observable<LoginResponse> {
    return this.http.post<LoginResponse>('/api/admin/jwtSettings', jwtSettings, defaultHttpOptionsFromConfig(config));
  }

  public saveRegistrationSettings(registrationSettings: RegistrationSettings,
    config?: RequestConfig): Observable<RegistrationSettings> {
    return this.http.post<RegistrationSettings>('/api/admin/selfRegistrationSettings', registrationSettings,
      defaultHttpOptionsFromConfig(config));
  }

  public getRegistrationSettings(config?: RequestConfig): Observable<RegistrationSettings> {
    return this.http.get<RegistrationSettings>(`/api/admin/selfRegistrationSettings`, defaultHttpOptionsFromConfig(config));
  }

  public deleteRegistrationSettings(config?: RequestConfig) {
    return this.http.delete('/api/admin/selfRegistrationSettings', defaultHttpOptionsFromConfig(config));
  }

  public getRepositorySettings(config?: RequestConfig): Observable<RepositorySettings> {
    return this.http.get<RepositorySettings>(`/api/admin/repositorySettings`, defaultHttpOptionsFromConfig(config));
  }

  public saveRepositorySettings(repositorySettings: RepositorySettings,
    config?: RequestConfig): Observable<RepositorySettings> {
    return this.http.post<RepositorySettings>('/api/admin/repositorySettings', repositorySettings,
      defaultHttpOptionsFromConfig(config)).pipe(
        tap(() => {
          this.entitiesVersionControlService.clearBranchList();
        })
      );
  }

  public deleteRepositorySettings(config?: RequestConfig) {
    return this.http.delete('/api/admin/repositorySettings', defaultHttpOptionsFromConfig(config)).pipe(
      tap(() => {
        this.entitiesVersionControlService.clearBranchList();
      })
    );
  }

  public checkRepositoryAccess(repositorySettings: RepositorySettings,
    config?: RequestConfig): Observable<void> {
    return this.http.post<void>('/api/admin/repositorySettings/checkAccess', repositorySettings, defaultHttpOptionsFromConfig(config));
  }

  public getRepositorySettingsInfo(config?: RequestConfig): Observable<RepositorySettingsInfo> {
    return this.http.get<RepositorySettingsInfo>('/api/admin/repositorySettings/info', defaultHttpOptionsFromConfig(config));
  }

  public getAutoCommitSettings(config?: RequestConfig): Observable<AutoCommitSettings> {
    return this.http.get<AutoCommitSettings>(`/api/admin/autoCommitSettings`, defaultHttpOptionsFromConfig(config));
  }

  public autoCommitSettingsExists(config?: RequestConfig): Observable<boolean> {
    return this.http.get<boolean>('/api/admin/autoCommitSettings/exists', defaultHttpOptionsFromConfig(config));
  }

  public saveAutoCommitSettings(autoCommitSettings: AutoCommitSettings,
    config?: RequestConfig): Observable<AutoCommitSettings> {
    return this.http.post<AutoCommitSettings>('/api/admin/autoCommitSettings', autoCommitSettings, defaultHttpOptionsFromConfig(config));
  }

  public deleteAutoCommitSettings(config?: RequestConfig) {
    return this.http.delete('/api/admin/autoCommitSettings', defaultHttpOptionsFromConfig(config));
  }

  public checkUpdates(config?: RequestConfig): Observable<UpdateMessage> {
    return this.http.get<UpdateMessage>(`/api/admin/updates`, defaultHttpOptionsFromConfig(config));
  }

  public getFeaturesInfo(config?: RequestConfig): Observable<FeaturesInfo> {
    return this.http.get<FeaturesInfo>('/api/admin/featuresInfo', defaultHttpOptionsFromConfig(config));
  }

  public getLoginProcessingUrl(config?: RequestConfig): Observable<string> {
    return this.http.get<string>(`/api/admin/mail/oauth2/loginProcessingUrl`, defaultHttpOptionsFromConfig(config));
  }

  public generateAccessToken(config?: RequestConfig): Observable<string> {
    return this.http.get<string>(`/api/admin/mail/oauth2/authorize`, defaultHttpOptionsFromConfig(config));
  }

  public getMailConfigTemplate(config?: RequestConfig): Observable<Array<MailConfigTemplate>> {
    return this.http.get<Array<MailConfigTemplate>>('/api/mail/config/template', defaultHttpOptionsFromConfig(config));
  }
}
