<template>
<div>
    <Upload
        ref="upload"
        :before-upload="handleUpload"
        :headers="headers"
        action="//jsonplaceholder.typicode.com/posts/">
        <Button icon="ios-cloud-upload-outline">选择excel文件上传</Button>
    </Upload>
    <div v-if="file != null">
        待上传文件: {{ file.name }}
        <Button type="text" @click="upload" :loading="loadingStatus">{{ loadingStatus ? '上传中' : '点击上传' }}</Button>
    </div>
</div>
</template>
<script>
import qs from 'qs'
export default {
    data(){
        return{
            file:'',
            loadingStatus:false,
            headers:{
                'Content-Type':'multipart/form-data'
            }
        }
    },
    methods:{
        handleUpload (file) {
            this.file = file;
            return false;
        },
        upload(){
            this.loadingStatus = true;
            let fileFormData = new FormData();
            fileFormData.append('file', this.file);
            let requestConfig = {
              headers: {
                'Content-Type': 'multipart/form-data'
              },
            }
            console.log(fileFormData);
            // this.$http.post('/upload', qs.stringify({fileFormData:fileFormData,processData: false, contentType: false})).then((res) => {
            //   console.log(res);
            // })
            this.$http.post('/upload', fileFormData,requestConfig).then((res) => {
              console.log(res);
            })
            this.loadingStatus = false;
            this.$refs.upload.clearFiles();
            this.file='';
        }
    }
}
</script>
<style scoped>

</style>

