<template>
    <div>
        <Upload
                ref="upload"
                name="upfile"
                :max-size="51200"
                :default-file-list="defaultList"
                :show-upload-list="false"
                :on-success="handleSuccess"
                :before-upload = "handleUpload"
                :action="uploadFileUrl"
                :headers="accessToken">
            <Button icon="ios-cloud-upload-outline">上传附件</Button>
        </Upload>
        <div v-for="(item, index) in files">Upload file: {{ item.name }}
            <a href="javascript:;" rel="external nofollow" @click="deleteFile(item.name)">X</a>
            <Button style="margin-left:30px;"
                    size="small"
                    v-if="index === 0"
                    type="primary"
                    @click="uploadFile"
                    :loading="loadingStatus">上传</Button>
        </div>
    </div>
</template>
<script>
export default {
    data(){
        return{
            defaultList:[],
            //上传文件的URL
            uploadFileUrl:"http://localhost:8181/upload",
            loadingStatus:false,
            //设置上传的请求头部
            accessToken:"",
            files:[],
            uploadFiles:[]
        }
    },
    methods:{
        init(){
            //设置上传的请求头部
            this.accessToken = {
                accessToken: this.getStore("accessToken")
            };
        },
        handleUpload(file){
            let keyID = Math.random().toString().substr(2);
            this.files['keyID'] = keyID;
            // 保存文件到总展示文件数组里
            this.files.push(file);
            // 保存文件到需要上传的文件数组里
            this.uploadFiles.push(file);
            // 返回 false 停止自动上传 我们需要手动上传
            return false;
        },
        deleteFile (name) { 
            // 删除文件
            // 删除展示文件里的当前文件
            this.files = this.files.filter(item => {
                return item.name != name
            })
            // 删除需要上传文件数据里的当前文件
            this.uploadFiles = this.uploadFiles.filter(item => {
                return item.KeyID != name
            })
        },
        uploadFile(){
            this.loadingStatus = true;
            this.$refs.upload.post(this.uploadFiles[0]);
            // for (let i = 0; i < this.uploadFile.length; i++) {
            //     let item = this.file[i];
            //     this.$refs.upload.post(item);
            // }
            this.loadingStatus = false;
        },
        handleSuccess(res,file){
            //上传成功后清空文件列表
            this.file = [];
            this.uploadFile = [];
            console.log(res);
            console.log(file);
        },	
    }
}
</script>
<style scoped>

</style>

