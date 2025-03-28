
import React from 'react';
import Dropzone from 'react-dropzone';
import ThingsboardBaseComponent from './json-form-base-component';
import { JsonFormFieldProps, JsonFormFieldState } from '@shared/components/json-form/react/json-form.models';
import IconButton from '@material-ui/core/IconButton';
import Clear from '@material-ui/icons/Clear';
import Tooltip from '@material-ui/core/Tooltip';

interface ThingsboardImageState extends JsonFormFieldState {
  imageUrl: string;
}

class ThingsboardImage extends React.Component<JsonFormFieldProps, ThingsboardImageState> {

    constructor(props) {
        super(props);
        this.onDrop = this.onDrop.bind(this);
        this.onClear = this.onClear.bind(this);
        const value = props.value ? props.value + '' : null;
        this.state = {
            imageUrl: value
        };
    }

    onDrop(acceptedFiles: File[]) {
      const reader = new FileReader();
      reader.onload = () => {
        this.onValueChanged(reader.result);
      };
      reader.readAsDataURL(acceptedFiles[0]);
    }

    onValueChanged(value) {
        this.setState({
            imageUrl: value
        });
        this.props.onChangeValidate({
            target: {
                value
            }
        });
    }

    onClear(event) {
        if (event) {
            event.stopPropagation();
        }
        this.onValueChanged('');
    }

    render() {

        let labelClass = 'tb-label';
        if (this.props.form.required) {
            labelClass += ' tb-required';
        }
        if (this.props.form.readonly) {
            labelClass += ' tb-readonly';
        }

        let previewComponent;
        if (this.state.imageUrl) {
            previewComponent = <img className='tb-image-preview' src={this.state.imageUrl} />;
        } else {
            previewComponent = <div>No image selected</div>;
        }

        return (
            <div className='tb-container'>
                <label className={labelClass}>{this.props.form.title}</label>
                <div className='tb-image-select-container'>
                    <div className='tb-image-preview-container'>{previewComponent}</div>
                    <div className='tb-image-clear-container'>
                        <Tooltip title='Clear' placement='top'>
                          <IconButton className='tb-image-clear-btn' onClick={this.onClear}><Clear/></IconButton>
                        </Tooltip>
                    </div>
                    <Dropzone onDrop={this.onDrop}
                              accept='image/*' multiple={false}>
                      {({getRootProps, getInputProps}) => (
                          <div className='tb-dropzone' {...getRootProps()}>
                            <div>Drop an image or click to select a file to upload.</div>
                            <input {...getInputProps()} />
                          </div>
                        )}
                    </Dropzone>
                </div>
            </div>
        );
    }
}

export default ThingsboardBaseComponent(ThingsboardImage);
