package hh.szu.web.servlet;

import hh.szu.domain.Category;
import hh.szu.domain.Product;
import hh.szu.sevice.AdminService;
import hh.szu.utils.CommonUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import sun.nio.ch.IOUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AdminAddProductServlet", urlPatterns = "/adminAddProduct")
public class AdminAddProductServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        //目的：收集表单数据，封装到一个Product实体中，将上传的图片保存到服务器上
        Product product = new Product();
        Map<String,Object> map = new HashMap<>();

        try {
            //创建磁盘文件项工厂
            DiskFileItemFactory factory = new DiskFileItemFactory();
            //创建文件上传核心对象
            ServletFileUpload upload = new ServletFileUpload(factory);
            //解析request获得文件项对象的集合
            List<FileItem> parseRequest = upload.parseRequest(request);
            for (FileItem item : parseRequest) {
                //判断是否为普通表单项
                boolean formField = item.isFormField();
                if (formField) {
                    //普通表单项，获得表单的数据，封装到Product实体中
                    String fieldName = item.getFieldName();
                    String fieldValue = item.getString("UTF-8");
                    map.put(fieldName,fieldValue);
                } else {
                    //文件上传项获得文件名称获得文件的内容
                    String fileName = item.getName();
                    String path = this.getServletContext().getRealPath("upload");
                    InputStream in = item.getInputStream();
                    OutputStream out = new FileOutputStream(path + "/" + fileName);
                    IOUtils.copy(in, out);
                    in.close();
                    out.close();
                    item.delete();
                    map.put("pimage",path + "/" + fileName);
                }
            }

            BeanUtils.populate(product,map);
            //完成product剩余的封装
            product.setPid(CommonUtils.getUUID());
            product.setPflag(0);
            Category category = new Category();
            category.setCid(map.get("cid").toString());
            product.setCategory(category);

            AdminService service = new AdminService();
            service.saveProduct(product);
        } catch (FileUploadException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
    }
}

