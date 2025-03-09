import { Component } from '@angular/core';

@Component({
  selector: 'tb-mail-templates',
  templateUrl: './mail-templates.component.html',
  styleUrls: ['./mail-templates.component.scss']
})
export class MailTemplatesComponent {

  editorConfig = {
    base_url: '/tinymce',
    suffix: '.min',
    toolbar_mode: 'floating',
    plugins: 'lists link image table wordcount fullscreen code',
    menubar: 'edit insert tools view format table',
    toolbar: 'fontfamily fontsize | blocks | bold italic strikethrough forecolor backcolor | link | table | image | alignleft aligncenter alignright alignjustify | indent outdent | removeformat | code | fullscreen',
  };

  isDisabledDelete: boolean = true;

  save() {
    console.log('Delete action triggered');
    // Implement the delete logic here
  }

}
