package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 日報保存
    @Transactional
    public ErrorKinds save(Report report) {

        // 日報重複チェック
//        if (findById(report.getId()) != null) {
//            return ErrorKinds.DUPLICATE_ERROR;
//        }

        report.setDeleteFlg(false);
        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    // 日報更新
    @Transactional
    public ErrorKinds update(Report report, Integer id) {
        Report oldReport = findById(id);

        oldReport.setReportDate(report.getReportDate());
        oldReport.setTitle(report.getTitle());
        oldReport.setContent(report.getContent());
        LocalDateTime now = LocalDateTime.now();
        oldReport.setUpdatedAt(now);

        reportRepository.save(oldReport);
        return ErrorKinds.SUCCESS;
    }

    // 日報削除
    @Transactional
    public ErrorKinds delete(Integer id) {

        Report report = findById(id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }

    // 日報一覧表示処理
    public List<Report> findAll() {
        Iterable<Report> iterable = reportRepository.findAll();
        List<Report> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }


    // 1件を検索
    public Report findById(Integer id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

    public List<Report> findByEmployee(Employee employee) {
        // TODO 自動生成されたメソッド・スタブ
        return reportRepository.findByEmployee(employee);
    }

}
