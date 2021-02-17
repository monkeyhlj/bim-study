<template>
  <div class="home">
    <!-- {{viewToken}} -->
    <div class="main">
      <div class="buttons">
        <button class="button" id="btnAddObject" @click="addExternalObject()">添加外部构件</button>
        <button class="button" id="btnRotateObject" @click="rotateObject()">旋转外部构件</button>
        <button class="button" id="btnMoveObject" @click="moveObject()">移动外部构件</button>
        <button class="button" id="btnScaleObject" @click="scaleObject()">放大外部构件</button>
        <button
          type="button"
          class="button"
          id="btnHideComponentsByObjectId"
          @click="hideComponentsById()"
        >根据ID隐藏构件</button>
        <button class="button" id="btnBlink" @click="blink()">开启强调效果</button>
        <button
          type="button"
          class="button"
          id="btnOverrideComponentsColorByObjectData"
          @click="overrideComponentsColorByData()"
        >给传感器着色</button>
        <button type="button" class="button" id="btnAddListener" @click="addListener()">添加点击监听事件</button>
        <button type="button" class="button" @click="getCameraState()">获取相机位置信息</button>
        <!-- <button type="button" class="button" @click="getCurrentState()">获取模型状态</button>
        <button type="button" class="button" @click="setState()">恢复模型状态</button>-->
      </div>
      
      
      <Modal v-model="modal1" title="控件信息" @on-ok="ok" @on-cancel="cancel">
        <Table border :columns="columns1" :data="data1"></Table>
      </Modal>
    </div>
    <!-- 定义DOM元素，用于在该DOM元素中显示模型或图纸 -->
    <div class="model" id="domId"></div>
  </div>
</template>

<script>
export default {
  name: "Home",
  data() {
    return {
      // viewToken:'',
      // viewToken:'351e823279a7405eb80f31d5d3e53108',
      viewToken: "403828aec82e488a9e1189084f151668",
      viewer3D: "",
      app: "",
      modelState: "",
      cameraState:
        '{"name":"persp","position":{"x":9608.61687276272,"y":18132.278790680644,"z":19467.8495371879},"target":{"x":51102.02116198803,"y":49107.73130508009,"z":-12186.725707312586},"up":{"x":0.41796734595966706,"y":0.3120146264461125,"z":0.8531999593267249},"fov":45,"zoom":1,"version":1,"coordinateSystem":"world"}',
      isEventListenerActivated: false,
      modal1: false,
      columns1: [
        {
          title: "objectId",
          key: "objectId"
        },
        {
          title: "modelId",
          key: "modelId"
        },
        {
          title: "elementId",
          key: "elementId"
        },
        {
          title: "objectType",
          key: "objectType"
        }
      ],
      data1: [],
      isOverrideComponentsColorByDataActivated: false,
      isBlinkComponentsActivated: false,
      isHideComponentsByIdActivated: false,
      isExternalObjectAdded: false,
      viewAdded: false,
      extObjMng: "",
      extObjId: "",
      isObjectScaled: false
    };
  },
  mounted() {
    // this.getViewToken();
    this.init();
  },
  methods: {
    getViewToken() {
      this.$http("/getviewtoken")
        .then(res => {
          console.log(res.data);
          this.viewToken = res.data.viewToken;
        })
        .catch(err => {
          console.log(err);
        });
    },

    init() {
      // 初始化显示组件
      var options = new BimfaceSDKLoaderConfig();
      options.viewToken = this.viewToken;
      BimfaceSDKLoader.load(
        options,
        this.successCallback,
        this.failureCallback
      );
    },

    successCallback(viewMetaData) {
      this.loadScript(
        "https://static.bimface.com/resources/ExternalObject/Loader/TDSLoader.js"
      );
      var that = this;
      if (viewMetaData.viewType == "3DView") {
        // ======== 判断是否为3D模型 ========
        // 获取DOM元素
        var dom4Show = document.getElementById("domId");
        var webAppConfig = new Glodon.Bimface.Application.WebApplication3DConfig();
        webAppConfig.domElement = dom4Show;
        // 创建WebApplication
        this.app = new Glodon.Bimface.Application.WebApplication3D(
          webAppConfig
        );
        // 添加待显示的模型
        this.app.addView(this.viewToken);
        // 从WebApplication获取viewer3D对象
        this.viewer3D = this.app.getViewer();
        // 监听添加view完成的事件
        this.viewer3D.addEventListener(
          Glodon.Bimface.Viewer.Viewer3DEvent.ViewAdded,
          () => {
            this.setCameraStatus1();
            this.viewAdded = true;
            //自适应屏幕大小
            window.onresize = function() {
              that.viewer3D.resize(
                document.documentElement.clientWidth,
                document.documentElement.clientHeight - 40
              );
            };
            // 调用viewer3D对象的Method，可以继续扩展功能
            // 渲染3D模型
            this.viewer3D.render();
          }
        );
      }
    },
    failureCallback(error) {
      console.log(error);
    },
    loadScript(url, callback) {
      var script = document.createElement("script");
      script.type = "text/javascript";
      script.onload = function() {
        callback && callback();
      };
      script.src = url;
      document.head.appendChild(script);
    },
    //   getCurrentState() {
    //       // 获取模型状态
    //       this.modelState = this.viewer3D.getCurrentState();
    //       console.log(this.modelState);
    //       this.viewer3D.render();
    //   },
    //   setState() {
    //       // 恢复模型状态
    //       this.viewer3D.setState(this.modelState);
    //       this.viewer3D.render();
    //   },
    setCameraStatus1() {
      this.viewer3D.setCameraStatus(this.cameraState);
    },
    getCameraState() {
      console.log(
        "相机位置信息：" + JSON.stringify(this.viewer3D.getCameraStatus())
      );
    },
    addListener() {
      if (!this.isEventListenerActivated) {
        this.viewer3D.addEventListener(
          Glodon.Bimface.Viewer.Viewer3DEvent.MouseClicked,
          this.getData
        );
        this.setButtonText("btnAddListener", "去除点击监听事件");
        this.isEventListenerActivated = true;
      } else {
        this.viewer3D.removeEventListener(
          Glodon.Bimface.Viewer.Viewer3DEvent.MouseClicked,
          this.getData
        );
        this.setButtonText("btnAddListener", "添加点击监听事件");
        this.isEventListenerActivated = false;
      }
    },
    getData(data) {
      //获取构件属性
      // console.log(JSON.stringify(data));
      console.log(data);
      var { objectId, elementId, modelId, objectType } = data;
      this.data1.push({ objectId, elementId, modelId, objectType });
      this.modal1 = true;
    },
    // *********************** 按钮文字 ***********************
    setButtonText(btnId, text) {
      var dom = document.getElementById(btnId);
      if (dom != null && dom.nodeName == "BUTTON") {
        dom.innerText = text;
      }
    },
    ok() {
      this.data1.pop();
    },
    cancel() {
      this.data1.pop();
    },
    overrideComponentsColorByData() {
      if (!this.isOverrideComponentsColorByDataActivated) {
        var color = new Glodon.Web.Graphics.Color("#FF0000", 0.8);
        //设置给构件着色的条件
        this.viewer3D.overrideComponentsColorByObjectData(
          [
            {
              categoryId: "6"
            }
          ],
          color
        );
        this.viewer3D.overrideComponentsOpacityByObjectData(
          [
            {
              levelName: "F2"
            }
          ],
          0.8
        );
        this.viewer3D.render();
        this.setButtonText(
          "btnOverrideComponentsColorByObjectData",
          "恢复颜色"
        );
      } else {
        //恢复颜色
        this.viewer3D.restoreComponentsColorByObjectData([
          {
            levelName: "F1"
          },
          {
            categoryId: "6"
          }
        ]);
        this.viewer3D.render();
        this.setButtonText(
          "btnOverrideComponentsColorByObjectData",
          "根据条件给构件着色"
        );
      }
      this.isOverrideComponentsColorByDataActivated = !this
        .isOverrideComponentsColorByDataActivated;
    },
    blink() {
      if (!this.isBlinkComponentsActivated) {
        // 构件闪烁
        this.viewer3D.addBlinkComponentsById(["161", "172"]);
        this.viewer3D.setBlinkColor(
          new Glodon.Web.Graphics.Color("#32D3A6", 0.6)
        );
        this.viewer3D.enableBlinkComponents(true);
        this.viewer3D.setBlinkIntervalTime(500);
        this.viewer3D.render();
        this.setButtonText("btnBlink", "关闭强调效果");
      } else {
        // 取消闪烁
        this.viewer3D.clearAllBlinkComponents();
        this.viewer3D.render();
        this.setButtonText("btnBlink", "开启强调效果");
      }
      this.isBlinkComponentsActivated = !this.isBlinkComponentsActivated;
    },
    hideComponentsById() {
      if (!this.isHideComponentsByIdActivated) {
        this.viewer3D.hideComponentsById(["172", "155"]);
        this.viewer3D.render();
        this.setButtonText("btnHideComponentsByObjectId", "显示构件");
      } else {
        //取消隐藏
        this.viewer3D.showComponentsById(["172", "155"]);
        this.viewer3D.render();
        this.setButtonText("btnHideComponentsByObjectId", "根据ID隐藏构件");
      }
      this.isHideComponentsByIdActivated = !this.isHideComponentsByIdActivated;
    },
    addExternalObject() {
      if (this.isExternalObjectAdded || !this.viewAdded) {
        return;
      }
      if (!this.extObjMng) {
        this.extObjMng = new Glodon.Bimface.Viewer.ExternalObjectManager(
          this.viewer3D
        );
      }
      var objUrl =
        "https://edutest11.oss-cn-beijing.aliyuncs.com/%E4%BC%A0%E6%84%9F%E5%99%A8001.3ds";
      // var objUrl = "https://static.bimface.com/attach/6db9d5bcf88640f997b23be61e870ee8_%E6%B1%BD%E8%BD%A6.3DS";
      // 构造3DS加载器
      var loader = new THREE.TDSLoader();
      var that = this;
      // 通过加载器加载资源，获取3DS对象
      loader.load(objUrl, function(object) {
        // 将该对象添加为外部构件
        that.extObjMng.addObject("vehicle", object);
        that.extObjId = that.extObjMng.getObjectIdByName("vehicle");
        that.isExternalObjectAdded = true;
        // 将构件移至初始位置
        that.extObjMng.translate(that.extObjId, {
          x: 22300,
          y: 33000,
          z: 4020
        });
        // that.extObjMng.translate(that.extObjId, {x: 23948, y: 33438, z: 4106});
        that.extObjMng.scale(that.extObjId, { x: 250.0, y: 250.0, z: 250.0 });
        that.viewer3D.render();
        // alert('添加成功！');
      });
    },
    rotateObject() {
      if (!this.isExternalObjectAdded || !this.viewAdded) {
        return;
      }
      // 绕逆时针旋转30°
      this.extObjMng.rotateZ(this.extObjId, Math.PI / 6);
    },
    moveObject() {
      if (!this.isExternalObjectAdded || !this.viewAdded) {
        return;
      }
      // 沿汽车前进方向移动1.0米
      this.extObjMng.offsetY(this.extObjId, -1000);
    },
    scaleObject() {
      if (!this.isExternalObjectAdded || !this.viewAdded) {
        return;
      }
      if (!this.isObjectScaled) {
        // 将汽车放大至当前的2倍
        this.extObjMng.scale(this.extObjId, { x: 2.0, y: 2.0, z: 2.0 });
        this.setButtonText("btnScaleObject", "缩小外部构件");
      } else {
        // 将汽车缩小至当前的0.5倍
        this.extObjMng.scale(this.extObjId, { x: 0.5, y: 0.5, z: 0.5 });
        this.setButtonText("btnScaleObject", "放大外部构件");
      }
      this.isObjectScaled = !this.isObjectScaled;
    }
  }
};
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
}

html,
body {
  height: 100%;
}

.main {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  height: 100%;
  width: 100%;
}

.model {
  /* flex: 1; */
  height: 700px;
  width: 100%;
}

.buttons {
  font-size: 10;
}

.button {
  margin: 5px 0 5px 5px;
  width: 130px;
  height: 30px;
  background: #11dab7;
  color: #ffffff;
  border-radius: 3px;
  border: none;
  cursor: pointer;
}
</style>

