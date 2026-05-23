package vn.hoidanit.jobhunter.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async // Cực kỳ quan trọng: Giúp gửi mail ngầm mà không làm chậm API rải CV
    public void sendNotificationEmail(String toEmail, String candidateName, String jobTitle, String companyName) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom("anhsp.lab@gmail.com", "Jobby Administrator");
            helper.setTo(toEmail);
            helper.setSubject("Jobby - Xác nhận ứng tuyển thành công: " + jobTitle);

            String htmlContent = "<div style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;\">" +
                    "    <h2 style=\"color: #1677ff; margin-bottom: 20px;\">Xác Nhận Nhận CV Thành Công!</h2>" +
                    "    <p>Xin chào <strong>" + candidateName + "</strong>,</p>" +
                    "    <p>Hệ thống kết nối việc làm <strong>Jobby</strong> xin thông báo: Bạn đã gửi CV thành công vào vị trí công việc dưới đây:</p>" +
                    "    <table style=\"width: 100%; border-collapse: collapse; margin: 20px 0; background-color: #fafafa;\">" +
                    "        <tr>" +
                    "            <td style=\"padding: 10px; border: 1px solid #ddd; font-weight: bold; width: 30%;\">Vị trí:</td>" +
                    "            <td style=\"padding: 10px; border: 1px solid #ddd;\">" + jobTitle + "</td>" +
                    "        </tr>" +
                    "        <tr>" +
                    "            <td style=\"padding: 10px; border: 1px solid #ddd; font-weight: bold;\">Công ty:</td>" +
                    "            <td style=\"padding: 10px; border: 1px solid #ddd;\">" + companyName + "</td>" +
                    "        </tr>" +
                    "    </table>" +
                    "    <p>CV của bạn đã được chuyển tới bộ phận Nhân sự của công ty. Phía nhà tuyển dụng sẽ đánh giá hồ sơ và gửi kết quả phản hồi đến bạn trong thời gian sớm nhất.</p>" +
                    "    <p style=\"margin-top: 30px; font-size: 13px; color: #888; border-top: 1px solid #eee; padding-top: 15px;\">" +
                    "        Đây là email tự động từ hệ thống Jobby. Vui lòng không phản hồi lại email này.<br>" +
                    "    </p>" +
                    "</div>";

            helper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
            System.out.println(">>> ĐÃ GỬI EMAIL THÔNG BÁO ỨNG TUYỂN ĐẾN: " + toEmail);

        } catch (Exception e) {
            // Đổi MessagingException thành Exception chung để bắt cả lỗi UnsupportedEncodingException do dùng tiếng Việt ở tên gửi
            System.err.println(">>> LỖI GỬI EMAIL: " + e.getMessage());
        }
    }
}