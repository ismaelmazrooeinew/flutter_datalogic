// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'scan_result.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ScanResult _$ScanResultFromJson(Map<String, dynamic> json) => ScanResult(
      status: $enumDecode(_$ScannerStatusTypeEnumMap, json['status']),
      data: json['scanData'] as String,
      dataId: json['scanDataId'] as String,
    );

Map<String, dynamic> _$ScanResultToJson(ScanResult instance) =>
    <String, dynamic>{
      'status': _$ScannerStatusTypeEnumMap[instance.status]!,
      'scanData': instance.data,
      'scanDataId': instance.dataId,
    };

const _$ScannerStatusTypeEnumMap = {
  ScannerStatusType.SCANNING: 'SCANNING',
  ScannerStatusType.IDLE: 'IDLE',
};
