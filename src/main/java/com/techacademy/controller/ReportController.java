package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // 日報一覧画面
    @GetMapping
    public String list(Model model) {

        model.addAttribute("listSize", reportService.findAll().size());
        model.addAttribute("reportsList", reportService.findAll());

        return "reports/list";
    }

    // 日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable Integer id, Model model) {

        model.addAttribute("report", reportService.findById(id));
        return "reports/detail";
    }

    // 日報更新画面を表示
    @GetMapping("/{id}/update")
    public String edit(@PathVariable("id") Integer id, Model model, Report report) {
        model.addAttribute("report", reportService.findById(id));
        // 日報更新画面に遷移
        return "reports/update";
    }

    // 日報更新処理
    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Integer id, @Validated Report report, BindingResult res, Model model) {

     // 入力チェック
        if (res.hasErrors()) {
            return edit(id, model, report);
        }

        reportService.update(report, id);
        // 一覧画面にリダイレクト
        return "redirect:/reports";

    }

    // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Report report,@AuthenticationPrincipal UserDetail userDetail, Model model) {
        model.addAttribute("loginUser", userDetail.getEmployee());
        return "reports/new";
    }

    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Report report, BindingResult res, Model model, @AuthenticationPrincipal UserDetail userDetail) {

        // 入力チェック
        if (res.hasErrors()) {
            return create(report, userDetail, model);
        }

        if (report.getId() != null) {
            // ログイン中の従業員かつ入力した日付の日報データが存在する場合エラー
            if(report.getReportDate() && Employee.loginUser());
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_ERROR));
        }
        Employee loginUser = userDetail.getEmployee(); //userDetail.getEmployee() ログインしている人の情報
        report.setEmployee(loginUser);
        reportService.save(report);
        // 一覧画面にリダイレクト
        return "redirect:/reports";

    }


    // 従業員削除処理
    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable Integer id, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        ErrorKinds result = reportService.delete(id);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("report", reportService.findById(id));
            return detail(id, model);
        }

        return "redirect:/reports";
    }
}
