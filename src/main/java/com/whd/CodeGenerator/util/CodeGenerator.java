package com.whd.CodeGenerator.util;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @Author weihaodong
 * @Date 2022/4/15 4:15 下午
 * @Description
 */
// 演示例子，执行 main 方法控制台输入模块表名回车自动生成对应项目目录中
public class CodeGenerator {

    private static final String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/database?useSSL=false";
    private static final String username = "xxxx";
    private static final String password = "xxxx";
    private static final String driverClassName = "com.mysql.cj.jdbc.Driver";

    private static final String parentPackagePath = "com.baomidou.ant";


    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        System.out.println(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotBlank(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");

        // 数据源配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUrl(jdbcUrl);
        // dataSourceConfig.setSchemaName("public");
        dataSourceConfig.setDriverName(driverClassName);
        dataSourceConfig.setUsername(username);
        dataSourceConfig.setPassword(password);

        // 策略配置 数据库表配置
        StrategyConfig strategyConfig = new StrategyConfig()
                .setNaming(NamingStrategy.underline_to_camel)
                .setColumnNaming(NamingStrategy.underline_to_camel)
                // .setSuperEntityClass("你自己的父类实体,没有就不用设置!")
                .setEntityLombokModel(true)
                .setRestControllerStyle(true)
                .setEntityTableFieldAnnotationEnable(false)
                // 公共父类
                //.setSuperControllerClass("你自己的父类控制器,没有就不用设置!")
                // 写于父类中的公共字段
                //.setSuperEntityColumns("id")
                .setInclude(scanner("表名，多个英文逗号分割").split(","))
                .setControllerMappingHyphenStyle(true);

        // 包配置
        PackageConfig packageConfig = new PackageConfig()
                // .setModuleName(scanner("模块名"))
                .setParent(parentPackagePath)
                .setEntity("entity")
                .setService("service")
                .setServiceImpl("service.impl")
                .setMapper("mapper")
                .setXml("mapper.xml")
                .setController("controller");
        Map<String, String> pathInfo = new HashMap<>();
        pathInfo.put(ConstVal.ENTITY_PATH, projectPath + "/src/main/java/" + parentPackagePath.replace('.', '/') + "/" + "entity");
        pathInfo.put(ConstVal.MAPPER_PATH, projectPath + "/src/main/java/" + parentPackagePath.replace('.', '/') + "/" + "mapper");
        pathInfo.put(ConstVal.XML_PATH, projectPath + "/src/main/java/" + parentPackagePath.replace('.', '/') + "/" + "mapper/xml");
        pathInfo.put(ConstVal.SERVICE_PATH, projectPath + "/src/main/java/" + parentPackagePath.replace('.', '/') + "/" + "service");
        pathInfo.put(ConstVal.SERVICE_IMPL_PATH, projectPath + "/src/main/java/" + parentPackagePath.replace('.', '/') + "/" + "service/impl");
        packageConfig.setPathInfo(pathInfo);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setOutputDir(projectPath + "/src/main/java");
        globalConfig.setFileOverride(true);
        globalConfig.setOpen(false);
        globalConfig.setAuthor("code generator");
        globalConfig.setSwagger2(false);
        globalConfig.setEntityName("%sEntity")
                .setMapperName("%sMapper")
                .setXmlName("%sMapper")
                .setServiceName("I%sMpService")
                .setServiceImplName("%sMpServiceImpl")
                .setControllerName("%sController");

        // 注入配置
        InjectionConfig injectionConfig = new InjectionConfig() {
            @Override
            public void initMap() {

            }
        };

        // 代码生成器
        AutoGenerator autoGenerator = new AutoGenerator()
                .setGlobalConfig(globalConfig)
                .setDataSource(dataSourceConfig)
                .setPackageInfo(packageConfig)
                .setCfg(injectionConfig)
                .setTemplate(templateConfig)
                .setStrategy(strategyConfig)
                .setTemplateEngine(new CustomFreemarkerTemplateEngine());

        autoGenerator.execute();
    }

    public static void buildFileOutConfig() {
        String templatePath = "/templates/mapper.xml.ftl";
        List<FileOutConfig> list = new ArrayList<>();
        // 当前项目路径
        String projectPath = System.getProperty("user.dir");

        // mapper xml文件输出
        list.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/resources/mapper/" + tableInfo.getEntityName() + StringPool.DOT_XML;
            }
        });
    }

}

class CustomFreemarkerTemplateEngine extends FreemarkerTemplateEngine {

    @Override
    public void writer(Map<String, Object> objectMap, String templatePath, String outputFile) throws Exception {
        super.writer(objectMap, templatePath, outputFile);
        String beforeContent = FileUtil.readString(new File(outputFile), StandardCharsets.UTF_8);
        String afterContent = beforeContent.replace("\r\n", "\n");
        boolean del = FileUtil.del(outputFile);
        logger.debug("del:" + del + ";  文件:" + outputFile);
        FileUtil.writeString(afterContent, new File(outputFile), StandardCharsets.UTF_8);
        logger.debug("模板:" + templatePath + ";  文件:" + outputFile + " 修改换行格式");
    }
}

