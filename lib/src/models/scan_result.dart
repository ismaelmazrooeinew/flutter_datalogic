import 'package:json_annotation/json_annotation.dart';

part 'scan_result.g.dart';

enum ScannerStatusType {
  /// Scanner has emitted the scan beam and scanning is in progress
  SCANNING,

  /// Scanner is in one of the following states: enabled but not yet in the waiting state, in the suspended state by an intent (e.g. SUSPEND_PLUGIN) or disabled due to the hardware trigger
  IDLE,
}

@JsonSerializable()
class ScanResult {
  ScanResult({
    required this.status,
    required this.data,
    required this.dataId,
  });

  @JsonKey(name: 'status')
  final ScannerStatusType status;

  @JsonKey(name: 'scanData')
  final String data;

  @JsonKey(name: 'scanDataId')
  final String dataId;

  factory ScanResult.fromJson(Map<String, dynamic> json) =>
      _$ScanResultFromJson(json);

  Map<String, dynamic> toJson() => _$ScanResultToJson(this);
}
