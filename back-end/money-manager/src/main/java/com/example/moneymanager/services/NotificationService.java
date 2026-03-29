package com.example.moneymanager.services;

import com.example.moneymanager.dto.ExpenseDto;
import com.example.moneymanager.entity.ProfileEntity;
import com.example.moneymanager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;

    @Value("${money.manager.frontend.url}")
    private String frontendUrl;

    @Scheduled(cron = "0 00 22 * * *", zone = "IST")
    public void sendDailyExpenseReminder() {
        log.info("Job Started: sendDailyExpenseReminder()");

        List<ProfileEntity> profiles = profileRepository.findAll();
        LocalDate today = LocalDate.now();

        for (ProfileEntity profile : profiles) {
            // Fetch today's expenses to show in the reminder table
            List<ExpenseDto> todaysExpenses = expenseService.getExpensesForUsersOnDate(profile.getId(), today);

            String htmlContent = buildSummaryEmailForExpenseNotification(profile.getFullName(), todaysExpenses, today);
            emailService.sendEmail(profile.getEmail(), "Daily Reminder: Log Your Transactions 💰", htmlContent);
        }
        log.info("Job Ended: sendDailyExpenseReminder()");
    }

    @Scheduled(cron = "0 38 21 * * *", zone = "IST")
    public void sendDailyExpenseSummary() {
        log.info("Job Started: sendDailyExpenseSummary() at 21:38 IST");

        List<ProfileEntity> profiles = profileRepository.findAll();
        LocalDate today = LocalDate.now();

        for (ProfileEntity profile : profiles) {
            List<ExpenseDto> todaysExpenses = expenseService.getExpensesForUsersOnDate(profile.getId(), today);
            String htmlContent = buildEmailSubjectWithTable(profile.getFullName(), todaysExpenses, today);
            emailService.sendEmail(profile.getEmail(), "Daily Expense Summary - " + today, htmlContent);
        }
    }

    private String buildEmailSubjectWithTable(String name, List<ExpenseDto> expenses, LocalDate date) {
        StringBuilder tableRows = new StringBuilder();
        int serialNo = 1;

        if (expenses.isEmpty()) {
            tableRows.append("<tr><td colspan='5' style='padding: 20px; text-align: center; color: #777777;'>No transactions logged yet for today.</td></tr>");
        } else {
            for (ExpenseDto ex : expenses) {
                tableRows.append(String.format(
                        "<tr style='border-bottom: 1px solid #eeeeee;'>" +
                                "<td style='padding: 10px; text-align: center;'>%d</td>" +
                                "<td style='padding: 10px;'>%s</td>" +
                                "<td style='padding: 10px; font-weight: bold;'>₹%s</td>" +
                                "<td style='padding: 10px;'>%s</td>" +
                                "<td style='padding: 10px;'>%s</td>" +
                                "</tr>",
                        serialNo++, ex.getName(), ex.getCategoryName(), ex.getId(),
                        date.toString()
                ));
            }
        }

        return "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f4f7f6; margin: 0; padding: 20px;'>" +
                "  <table align='center' border='0' cellpadding='0' cellspacing='0' width='100%' style='max-width: 650px; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.1);'>" +
                "    <tr>" +
                "      <td style='padding: 30px; text-align: center; background-color: #4CAF50; border-radius: 10px 10px 0 0;'>" +
                "        <h1 style='color: #ffffff; margin: 0; font-size: 24px;'>Money Manager</h1>" +
                "      </td>" +
                "    </tr>" +
                "    <tr>" +
                "      <td style='padding: 30px; color: #333333; line-height: 1.6;'>" +
                "        <h2 style='margin-top: 0;'>Hello, " + name + "! 👋</h2>" +
                "        <p>It's 10:00 PM—time to review your logs. Here is what you've recorded so far:</p>" +
                "        <table style='width: 100%; border-collapse: collapse; margin-top: 15px; font-size: 14px;'>" +
                "          <thead>" +
                "            <tr style='background-color: #f8f9fa; border-bottom: 2px solid #4CAF50; text-align: left;'>" +
                "              <th style='padding: 10px;'>S.No</th>" +
                "              <th style='padding: 10px;'>Name</th>" +
                "              <th style='padding: 10px;'>Amount</th>" +
                "              <th style='padding: 10px;'>Category</th>" +
                "              <th style='padding: 10px;'>Date</th>" +
                "            </tr>" +
                "          </thead>" +
                "          <tbody>" + tableRows.toString() + "</tbody>" +
                "        </table>" +
                "        <p style='margin-top: 20px;'>Missing something? Click below to add more entries.</p>" +
                "        <div style='text-align: center; margin: 25px 0;'>" +
                "          <a href='" + frontendUrl + "' style='background-color: #4CAF50; color: white; padding: 12px 25px; text-decoration: none; font-weight: bold; border-radius: 5px;'>Update Daily Logs</a>" +
                "        </div>" +
                "      </td>" +
                "    </tr>" +
                "    <tr>" +
                "      <td style='padding: 20px; text-align: center; font-size: 12px; color: #999999; background-color: #f9f9f9; border-radius: 0 0 10px 10px;'>" +
                "        &copy; 2026 Money Manager Team | Tracking your way to freedom." +
                "      </td>" +
                "    </tr>" +
                "  </table>" +
                "</body>" +
                "</html>";
    }

    private String buildSummaryEmailForExpenseNotification(String name, List<ExpenseDto> expenses, LocalDate date) {
        StringBuilder tableRows = new StringBuilder();
        BigDecimal totalAmount = BigDecimal.ZERO;

        if (expenses.isEmpty()) {
            tableRows.append("<tr><td colspan='3' style='padding: 20px; text-align: center; color: #94a3b8;'>No expenses logged today.</td></tr>");
        } else {
            for (ExpenseDto ex : expenses) {
                totalAmount = totalAmount.add(ex.getAmount());
                tableRows.append(String.format(
                        "<tr style='border-bottom: 1px solid #e2e8f0;'>" +
                                "<td style='padding: 12px;'>%s</td>" +
                                "<td style='padding: 12px;'>%s</td>" +
                                "<td style='padding: 12px; text-align: right; font-weight: bold;'>₹%s</td>" +
                                "</tr>",
                        ex.getName(), ex.getCategoryName(), ex.getAmount()
                ));
            }
        }

        return "<html><body style='font-family: sans-serif; background-color: #f8fafc; padding: 20px;'>" +
                "  <div style='max-width: 600px; margin: auto; background: #ffffff; border-radius: 12px; overflow: hidden; border: 1px solid #e2e8f0;'>" +
                "    <div style='background-color: #10b981; padding: 25px; text-align: center;'>" +
                "      <h1 style='color: white; margin: 0; font-size: 20px;'>Daily Spending Report</h1>" +
                "      <p style='color: #d1fae5; margin: 5px 0 0 0;'>Summary for " + date + "</p>" +
                "    </div>" +
                "    <div style='padding: 30px;'>" +
                "      <h2 style='font-size: 18px; color: #1e293b;'>Hello, " + name + "</h2>" +
                "      <p style='color: #64748b;'>Here is a breakdown of your spending today:</p>" +
                "      <table style='width: 100%; border-collapse: collapse; margin-top: 20px;'>" +
                "        <thead>" +
                "          <tr style='background-color: #f1f5f9; text-align: left; color: #475569;'>" +
                "            <th style='padding: 12px;'>Expense</th>" +
                "            <th style='padding: 12px;'>Category</th>" +
                "            <th style='padding: 12px; text-align: right;'>Amount</th>" +
                "          </tr>" +
                "        </thead>" +
                "        <tbody>" + tableRows + "</tbody>" +
                "        <tfoot>" +
                "          <tr style='background-color: #f8fafc; font-weight: bold;'>" +
                "            <td colspan='2' style='padding: 15px; text-align: right;'>Total Today:</td>" +
                "            <td style='padding: 15px; text-align: right; color: #10b981; font-size: 18px;'>₹" + totalAmount + "</td>" +
                "          </tr>" +
                "        </tfoot>" +
                "      </table>" +
                "      <div style='text-align: center; margin-top: 30px;'>" +
                "        <a href='" + frontendUrl + "' style='background-color: #1e293b; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold;'>View Dashboard</a>" +
                "      </div>" +
                "    </div>" +
                "  </div>" +
                "</body></html>";
    }
}