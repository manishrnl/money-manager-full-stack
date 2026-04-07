package com.example.moneymanager.services;

import com.example.moneymanager.dto.ExpenseDto;
import com.example.moneymanager.entity.ProfileEntity;
import com.example.moneymanager.repository.CategoryRepository;
import com.example.moneymanager.repository.IncomeRepository;
import com.example.moneymanager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;

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

    @Scheduled(cron = "0 30 21 * * *", zone = "IST")
    public void sendDailyExpenseSummary() {
        log.info("Job Started: sendDailyExpenseSummary() at 21:30 IST");

        List<ProfileEntity> profiles = profileRepository.findAll();
        LocalDate today = LocalDate.now();

        for (ProfileEntity profile : profiles) {
            List<ExpenseDto> todaysExpenses = expenseService.getExpensesForUsersOnDate(profile.getId(), today);
            String htmlContent = buildEmailSubjectWithTable(profile.getFullName(), todaysExpenses, today);
            emailService.sendEmail(profile.getEmail(), "Daily Expense Summary - " + today, htmlContent);
        }
    }

    private String buildEmailSubjectWithTable(String name, List<ExpenseDto> expenses, LocalDate date) {
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");// Use "hh:mm a" for 12-hour format with AM/PM
        String formattedTime = currentTime.format(formatter);

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
                        serialNo++, ex.getName(), ex.getAmount(), ex.getCategoryName(),
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
                "        <p>It's " + formattedTime + " —time to review your logs. Here is what you've recorded so far:</p>" +
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
        frontendUrl += frontendUrl + "/expense";
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
                "        <a href='" + frontendUrl + "' style='background-color: #1e293b; " +
                "color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold;'>View Dashboard</a>" +
                "      </div>" +
                "    </div>" +
                "  </div>" +
                "</body></html>";
    }

    public void sendIncomesNotificationFromFrontend(String toEmail, List<Map<String, Object>> incomeDataList) {
        log.info("Triggering Email sending process using frontend data for: {}", toEmail);

        ProfileEntity currentUser = profileService.getCurrentProfile();

        // Build HTML using the data sent in the request body
        String htmlContent = buildIncomesHtml(
                currentUser.getFullName(),
                incomeDataList,
                toEmail
        );

        emailService.sendEmail(toEmail, "Your Income Report - Money Manager", htmlContent);
    }

    private String buildIncomesHtml(String fullName, List<Map<String, Object>> dataList, String toEmail) {
        StringBuilder tableRows = new StringBuilder();
        int serialNo = 1;

        if (dataList == null || dataList.isEmpty()) {
            tableRows.append("<tr><td colspan='5' style='padding: 20px; text-align: center; color: #777777;'>No transactions found in current view.</td></tr>");
        } else {
            for (Map<String, Object> item : dataList) {
                // We extract fields based on the JSON keys sent by your React Frontend
                String name = String.valueOf(item.getOrDefault("name", "N/A"));
                String amount = String.valueOf(item.getOrDefault("amount", "0"));
                String date = String.valueOf(item.getOrDefault("date", "N/A"));

                tableRows.append(String.format(
                        "<tr style='border-bottom: 1px solid #eeeeee;'>" +
                                "<td style='padding: 10px; text-align: center;'>%d</td>" +
                                "<td style='padding: 10px;'>%s</td>" +
                                "<td style='padding: 10px; font-weight: bold; color: #2e7d32;'>₹%s</td>" +
                                "<td style='padding: 10px;'>%s</td>" +
                                "<td style='padding: 10px; color: #666;'>%s</td>" +
                                "</tr>",
                        serialNo++, name, amount, "INCOME", date
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
                "        <h2 style='margin-top: 0;'>Hello, " + fullName + "! 👋</h2>" +
                "        <p>Here is the income report generated from your current dashboard view.</p>" +
                "        <table style='width: 100%; border-collapse: collapse; margin-top: 15px; font-size: 14px;'>" +
                "          <thead>" +
                "            <tr style='background-color: #f8f9fa; border-bottom: 2px solid #4CAF50; text-align: left;'>" +
                "              <th style='padding: 10px;'>Sl . No</th>" +
                "              <th style='padding: 10px;'>Income Source</th>" +
                "              <th style='padding: 10px;'>Amount</th>" +
                "              <th style='padding: 10px;'>Category Type</th>" +
                "              <th style='padding: 10px;'>Date</th>" +
                "            </tr>" +
                "          </thead>" +
                "          <tbody>" + tableRows.toString() + "</tbody>" +
                "        </table>" +
                "        <div style='text-align: center; margin: 25px 0;'>" +
                "          <a href='" + frontendUrl + "/income' style='background-color: #4CAF50; " +
                "color:" +
                " " +
                "white; padding: 12px 25px; text-decoration: none; font-weight: bold; border-radius: 5px;'>View Dashboard</a>" +
                "        </div>" +
                "      </td>" +
                "    </tr>" +
                "    <tr>" +
                "      <td style='padding: 20px; text-align: center; font-size: 11px; color: #999999; background-color: #f9f9f9; border-radius: 0 0 10px 10px;'>" +
                "        This report was generated instantly from your active session. <br/>" +
                "        &copy; 2026 Money Manager Team" +
                "      </td>" +
                "    </tr>" +
                "  </table>" +
                "</body>" +
                "</html>";
    }

    public void sendExpensesNotificationFromFrontend(String toEmail, List<Map<String, Object>> expenseDataList) {
        log.info("Triggering Email sending process using frontend data for: {}", toEmail);

        ProfileEntity currentUser = profileService.getCurrentProfile();

        // Build HTML using the data sent in the request body
        String htmlContent = buildExpensesHtml(
                currentUser.getFullName(),
                expenseDataList,
                toEmail
        );

        emailService.sendEmail(toEmail, "Your Expense Report - Money Manager", htmlContent);
    }

    private String buildExpensesHtml(String fullName, List<Map<String, Object>> dataList, String toEmail) {
        StringBuilder tableRows = new StringBuilder();
        int serialNo = 1;

        if (dataList == null || dataList.isEmpty()) {
            tableRows.append("<tr><td colspan='5' style='padding: 20px; text-align: center; color: #777777;'>No expense transactions found.</td></tr>");
        } else {
            for (Map<String, Object> item : dataList) {
                String name = String.valueOf(item.getOrDefault("name", "N/A"));
                String amount = String.valueOf(item.getOrDefault("amount", "0"));
                String date = String.valueOf(item.getOrDefault("date", "N/A"));

                tableRows.append(String.format(
                        "<tr style='border-bottom: 1px solid #eeeeee;'>" +
                                "<td style='padding: 10px; text-align: center;'>%d</td>" +
                                "<td style='padding: 10px;'>%s</td>" +
                                "<td style='padding: 10px; font-weight: bold; color: #d32f2f;'>₹%s</td>" + // Red color for expenses
                                "<td style='padding: 10px;'>%s</td>" +
                                "<td style='padding: 10px; color: #666;'>%s</td>" +
                                "</tr>",
                        serialNo++, name, amount, "EXPENSE", date
                ));
            }
        }

        return "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f4f7f6; margin: 0; padding: 20px;'>" +
                "  <table align='center' border='0' cellpadding='0' cellspacing='0' width='100%' style='max-width: 650px; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.1);'>" +
                "    <tr>" +
                "      <td style='padding: 30px; text-align: center; background-color: #e53935; border-radius: 10px 10px 0 0;'>" + // Red header for expenses
                "        <h1 style='color: #ffffff; margin: 0; font-size: 24px;'>Money Manager</h1>" +
                "      </td>" +
                "    </tr>" +
                "    <tr>" +
                "      <td style='padding: 30px; color: #333333; line-height: 1.6;'>" +
                "        <h2 style='margin-top: 0;'>Hello, " + fullName + "! 👋</h2>" +
                "        <p>Here is the <strong>expense report</strong> generated from your current dashboard view.</p>" +
                "        <table style='width: 100%; border-collapse: collapse; margin-top: 15px; font-size: 14px;'>" +
                "          <thead>" +
                "            <tr style='background-color: #f8f9fa; border-bottom: 2px solid #e53935; text-align: left;'>" +
                "              <th style='padding: 10px;'>Sl. No</th>" +
                "              <th style='padding: 10px;'>Transaction Source</th>" +
                "              <th style='padding: 10px;'>Amount</th>" +
                "              <th style='padding: 10px;'>Type</th>" +
                "              <th style='padding: 10px;'>Date</th>" +
                "            </tr>" +
                "          </thead>" +
                "          <tbody>" + tableRows.toString() + "</tbody>" +
                "        </table>" +
                "        <div style='text-align: center; margin: 25px 0;'>" +
                "          <a href='" + frontendUrl + "/expense' style='background-color: #e53935; color: white; padding: 12px 25px; text-decoration: none; font-weight: bold; border-radius: 5px;'>View Expenses</a>" +
                "        </div>" +
                "      </td>" +
                "    </tr>" +
                "    <tr>" +
                "      <td style='padding: 20px; text-align: center; font-size: 11px; color: #999999; background-color: #f9f9f9; border-radius: 0 0 10px 10px;'>" +
                "        This report was generated instantly from your active session. <br/>" +
                "        &copy; 2026 Money Manager Team" +
                "      </td>" +
                "    </tr>" +
                "  </table>" +
                "</body>" +
                "</html>";
    }

    public void sendFilteredNotificationFromFrontend(String toEmail, List<Map<String, Object>> filteredBody) {
        log.info("Triggering Combined Email report for: {} with {} items", toEmail, filteredBody.size());

        ProfileEntity currentUser = profileService.getCurrentProfile();

        // Build HTML with dynamic type handling
        String htmlContent = buildFilteredHtml(
                currentUser.getFullName(),
                filteredBody,
                toEmail
        );

        emailService.sendEmail(toEmail, "Financial Statement - Money Manager", htmlContent);
    }
    private String buildFilteredHtml(String fullName, List<Map<String, Object>> dataList, String toEmail) {
        StringBuilder tableRows = new StringBuilder();
        int serialNo = 1;

        if (dataList == null || dataList.isEmpty()) {
            tableRows.append("<tr><td colspan='5' style='padding: 20px; text-align: center; color: #777777;'>No transactions found.</td></tr>");
        } else {
            for (Map<String, Object> item : dataList) {
                // 1. Extract and Clean Data
                String name = String.valueOf(item.getOrDefault("name", item.getOrDefault("source", "N/A")));
                String amount = String.valueOf(item.getOrDefault("amount", "0"));
                String date = String.valueOf(item.getOrDefault("date", "N/A"));

                // 2. Dynamic Type Check (Handling 'type' or 'transactionType' keys)
                Object typeObj = item.get("type") != null ? item.get("type") : item.get("transactionType");
                String rawType = (typeObj != null) ? String.valueOf(typeObj).trim().toUpperCase() : "EXPENSE";

                // 3. Determine Styles based on the DATA, not hardcoded strings
                boolean isIncome = rawType.equals("INCOME");
                String typeColor = isIncome ? "#10b981" : "#ef4444"; // Emerald Green vs Red
                String bgColor = isIncome ? "#ecfdf5" : "#fef2f2";   // Very light green vs very light red

                tableRows.append(String.format(
                        "<tr style='border-bottom: 1px solid #f1f5f9;'>" +
                                "<td style='padding: 12px; text-align: center; color: #94a3b8;'>%d</td>" +
                                "<td style='padding: 12px; font-weight: 600; color: #1e293b;'>%s</td>" +
                                "<td style='padding: 12px; font-weight: bold; color: %s;'>%s ₹%s</td>" +
                                "<td style='padding: 12px;'>" +
                                "<span style='background-color: %s; color: %s; padding: 4px 10px; border-radius: 6px; font-size: 10px; font-weight: 800; text-transform: uppercase;'>%s</span>" +
                                "</td>" +
                                "<td style='padding: 12px; color: #64748b; font-size: 12px;'>%s</td>" +
                                "</tr>",
                        serialNo++,
                        name,
                        typeColor, (isIncome ? "+" : "-"), amount, // Adds +/- sign based on data
                        bgColor, typeColor, rawType,               // Background, Text, and Label from data
                        date
                ));
            }
        }
        return "<html>" +
                "<body style='font-family: \"Segoe UI\", Helvetica, Arial, sans-serif; background-color: #f8fafc; margin: 0; padding: 20px;'>" +
                "  <table align='center' border='0' cellpadding='0' cellspacing='0' width='100%' style='max-width: 650px; background-color: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 10px 25px rgba(0,0,0,0.05);'>" +
                "    <tr>" +
                "      <td style='padding: 40px; text-align: center; background-color: #0f172a;'>" + // Dark slate header (Premium Look)
                "        <h1 style='color: #ffffff; margin: 0; font-size: 26px; letter-spacing: -1px;'>Money Manager</h1>" +
                "        <p style='color: #94a3b8; margin: 5px 0 0 0; font-size: 14px;'>Filtered Statement Report</p>" +
                "      </td>" +
                "    </tr>" +
                "    <tr>" +
                "      <td style='padding: 40px; color: #1e293b; line-height: 1.6;'>" +
                "        <h2 style='margin-top: 0; font-size: 20px;'>Hello, " + fullName + "! 👋</h2>" +
                "        <p style='color: #64748b;'>Below is the summary of your <strong>Income & Expenses</strong> based on your current filtered view.</p>" +
                "        <table style='width: 100%; border-collapse: collapse; margin-top: 25px;'>" +
                "          <thead>" +
                "            <tr style='background-color: #f1f5f9; text-align: left;'>" +
                "              <th style='padding: 12px; font-size: 11px; text-transform: uppercase; color: #64748b; text-align: center;'>#</th>" +
                "              <th style='padding: 12px; font-size: 11px; text-transform: uppercase; color: #64748b;'>Description</th>" +
                "              <th style='padding: 12px; font-size: 11px; text-transform: uppercase; color: #64748b;'>Amount</th>" +
                "              <th style='padding: 12px; font-size: 11px; text-transform: uppercase; color: #64748b;'>Type</th>" +
                "              <th style='padding: 12px; font-size: 11px; text-transform: uppercase; color: #64748b;'>Date</th>" +
                "            </tr>" +
                "          </thead>" +
                "          <tbody style='font-size: 14px;'>" + tableRows.toString() + "</tbody>" +
                "        </table>" +
                "      </td>" +
                "    </tr>" +
                "    <tr>" +
                "      <td style='padding: 30px; text-align: center; background-color: #f8fafc; border-top: 1px solid #e2e8f0; font-size: 12px; color: #94a3b8;'>" +
                "        This is an automated report generated from your dashboard session.<br/>" +
                "        <strong>Money Manager Pro &copy; 2026</strong>" +
                "      </td>" +
                "    </tr>" +
                "  </table>" +
                "</body>" +
                "</html>";
    }
}